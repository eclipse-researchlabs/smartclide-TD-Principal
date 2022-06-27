package gr.nikos.smartclideTDPrincipal.service;

/*
 * Copyright (C) 2021 UoM - University of Macedonia
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 */

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import gr.nikos.smartclideTDPrincipal.controller.entity.RequestBodyAnalysis;
import gr.nikos.smartclideTDPrincipal.model.Issue;
import gr.nikos.smartclideTDPrincipal.model.Metric;
import gr.nikos.smartclideTDPrincipal.service.analysis.SonarAnalysis;
import gr.nikos.smartclideTDPrincipal.service.analysis.Git;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.util.FileSystemUtils;

@Service
public class AnalysisService {
	
	@Value("${gr.nikos.smartclide.sonarqube.url}")
	private String sonarQubeUrl;
	
	/**
	 * Start a new Sonar analysis 
	 * @param requestBodyAnalysis the required git parameters
	 */
	public String startNewAnalysis(RequestBodyAnalysis requestBodyAnalysis) {
		String report="";
        try {
            //setup sonarqube instance to sonarscanner
            /*FileWriter fstream = new FileWriter("/sonar-scanner-4.6.2.2472-linux/conf/sonar-scanner.properties", false);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("sonar.host.url="+sonarQubeUrl);
            out.close();*/

            //Git clone
            Git gitProject = new Git(requestBodyAnalysis.getGitURL(),requestBodyAnalysis.getToken());
            gitProject.cloneGit();
            report+= gitProject.getReport();

	        //create sonar-project.properties
            report+= "; sonar.projectKey:"+gitProject.getOwnerName() +":"+ gitProject.getProjectName()+"; ";
            SonarAnalysis analysis = new SonarAnalysis(gitProject);
            analysis.createSonarFile();
	        
	        //start analysis
	        analysis.makeSonarAnalysis();
            report+= analysis.getReport();

            //delete clone
            FileSystemUtils.deleteRecursively(new File(gitProject.getLocalPath()));
		} catch (IOException e) {
            report="error::"+e.toString();
			e.printStackTrace();
		}
        return report;
	}

	/**
	 * Get 2 metrics from SonarQube API
	 * @param projectKey the project key
	 * @return
	 */
	public Metric[] getMeasures(String projectKey) {
		try {
			Metric[] metrics= new Metric[2];
            URL url = new URL(sonarQubeUrl + "/api/measures/component?component="+projectKey+
				                "&metricKeys=sqale_index,code_smells");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200)
                    System.err.println(responsecode);
            else{
                Scanner sc = new Scanner(url.openStream());
                String inline="";
                while(sc.hasNext()){
                    inline+=sc.nextLine();
                }
                sc.close();

                JSONParser parse = new JSONParser();
                JSONObject jobj = (JSONObject)parse.parse(inline);
                JSONObject jobj1= (JSONObject) jobj.get("component");
                JSONArray jsonarr_1 = (JSONArray) jobj1.get("measures");

                for(int i=0; i<jsonarr_1.size(); i++){
                    JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(i);
                    if(jsonobj_1.get("metric").toString().equals("sqale_index"))
                    	metrics[0]= new Metric("sqale_index", Integer.parseInt(jsonobj_1.get("value").toString()));
                    if(jsonobj_1.get("metric").toString().equals("code_smells"))
                    	metrics[1]= new Metric("code_smells", Integer.parseInt(jsonobj_1.get("value").toString()));
                }
            }
    		return metrics;
		} catch (IOException | org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get all the issues for the SonarQube API
	 * @param projectKey the project key
	 * @return
	 */
	public List<Issue> getIssues(String projectKey) {
		//initialization
		Metric[] metrics= getMeasures(projectKey);
		int totalCodeSmells;
		if(metrics[0].getName().equals("code_smells"))
			totalCodeSmells=metrics[0].getValue();
		else
			totalCodeSmells=metrics[1].getValue();
		int page= (totalCodeSmells-1)/500 + 1;
		List<Issue> issuesList =new ArrayList<Issue>();
		
		//if there are more than limit of API 10,000 split 
        if(totalCodeSmells>10000){
        	int issuesN= getIssuesNumbers(projectKey, "&resolved=false&severities=INFO");
            page= (issuesN-1)/500 + 1;
            if(issuesN>0){
                for(int i=1; i<=page; i++){
                	issuesList.addAll(getIssuesFromPage(projectKey, 1, "&resolved=false&severities=INFO"));
                }
            }
            
            issuesN= getIssuesNumbers(projectKey, "&resolved=false&severities=MINOR,MAJOR,CRITICAL,BLOCKER");
            page= (issuesN-1)/500 + 1;
            if(issuesN>0 && issuesN<10000){
                for(int i=1; i<=page; i++){
                	issuesList.addAll(getIssuesFromPage(projectKey, i, "&resolved=false&severities=MINOR,MAJOR,CRITICAL,BLOCKER"));
                }
            }
            // if again more than 10,000, then split again
            else if(issuesN>1000){
                page= (getIssuesNumbers(projectKey, "&resolved=false&severities=MINOR,MAJOR")-1)/500 + 1;
                for(int i=1; i<=page; i++){
                	issuesList.addAll(getIssuesFromPage(projectKey, 1, "&resolved=false&severities=MINOR,MAJOR"));
                }
                
                page= (getIssuesNumbers(projectKey, "&resolved=false&severities=CRITICAL,BLOCKER")-1)/500 + 1;
                for(int i=1; i<=page; i++){
                	issuesList.addAll(getIssuesFromPage(projectKey, i, "&resolved=false&severities=CRITICAL,BLOCKER"));
                }
            }
        }
        else{   //if rules less than 10,000 then all together
            for(int i=1; i<=page; i++){
            	issuesList.addAll(getIssuesFromPage(projectKey, i, "&resolved=false"));
            }
        }
        
		return issuesList;
	}
	
	/**
     * Get number of issues for a specific API call
     * @param extra the severities we want each time
     */
    private int getIssuesNumbers(String projectKey, String extra){
    	try {
            URL url = new URL(sonarQubeUrl + "/api/issues/search?pageSize=500&componentKeys="
				        +projectKey+"&types=CODE_SMELL"+extra+"&p=1");
			
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: "+responsecode);
            else{
                Scanner sc = new Scanner(url.openStream());
                String inline="";
                while(sc.hasNext()){
                    inline+=sc.nextLine();
                }
                String number =inline.split(",",2)[0].replace("{\"total\":", "");
                sc.close();
                return Integer.parseInt(number);
            }
    	} catch (IOException e) {
			e.printStackTrace();
		}
        return 0;
    }
    
    /**
     * Get Issues From Sonar API
     * @param projectKey the key of the Project
     * @param page the results
     * @param extra extra filtering
     */
    private List<Issue> getIssuesFromPage(String projectKey, int page, String extra){
        try {
            URL url = new URL(sonarQubeUrl + "/api/issues/search?ps=500&componentKeys="
                    +projectKey+"&types=CODE_SMELL"+extra+"&p="+page);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: "+responsecode);
            else{
                Scanner sc = new Scanner(url.openStream());
                String inline="";
                while(sc.hasNext()){
                    inline+=sc.nextLine();
                }
                sc.close();
                List<Issue> issuesList = new ArrayList<Issue>();
                
                JSONParser parse = new JSONParser();
                JSONObject jobj = (JSONObject)parse.parse(inline);
                JSONArray jsonarr_1 = (JSONArray) jobj.get("issues");
                for(int i=0;i<jsonarr_1.size();i++){
                    JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(i);
                    JSONObject jsonobj_2=(JSONObject)jsonobj_1.get("textRange");
                    Issue issue;
                    String debt;
                    if(jsonobj_1.get("debt") == null)
                    	debt = "0min";
                    else
                    	debt = jsonobj_1.get("debt").toString();
                    if(jsonobj_2 != null)
                    	issue=new Issue(jsonobj_1.get("rule").toString(), jsonobj_1.get("message").toString()
                            , jsonobj_1.get("severity").toString(), debt
                            , jsonobj_1.get("type").toString(), jsonobj_1.get("component").toString()
                            , jsonobj_2.get("startLine").toString(), jsonobj_2.get("endLine").toString());
                    else
                    	issue=new Issue(jsonobj_1.get("rule").toString(), jsonobj_1.get("message").toString()
                                , jsonobj_1.get("severity").toString(), jsonobj_1.get("debt").toString()
                                , jsonobj_1.get("type").toString(), jsonobj_1.get("component").toString()
                                , "0", "1");
                    issuesList.add(issue);
                }
                return issuesList;
            }
        } catch (IOException|ParseException e) {
			e.printStackTrace();
		}
		return null;
    }
}
