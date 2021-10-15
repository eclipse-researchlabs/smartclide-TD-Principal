package gr.nikos.smartclideTDPrincipal.Analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import gr.nikos.smartclideTDPrincipal.SmartclideTdPrincipalApplication;

@Service
public class AnalysisService {
	
	
	/**
	 * Start a new Sonar analysis 
	 * @param requestBodyAnalysis the required git parameters
	 */
	public void startNewAnalysis(RequestBodyAnalysis requestBodyAnalysis) {
		try {
			//mkdir
			ProcessBuilder pbuilder = new ProcessBuilder("bash", "-c", "mkdir -p tmp");
	        Process p = pbuilder.start();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line;
	        while ((line = reader.readLine()) != null) {
	            System.out.println(line);
	        }
	        
	        //clone
	        ProcessBuilder pbuilder1 = new ProcessBuilder("bash", "-c", "cd tmp; git clone "+requestBodyAnalysis.getGitURL());
	        Process p1 = pbuilder1.start();
	        BufferedReader reader1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
	        String line1;
	        while ((line1 = reader1.readLine()) != null) {
	            System.out.println(line1);
	        }
	        
	        //create sonar-project.properties
	        BufferedWriter writer = new BufferedWriter(new FileWriter("/tmp/"+ requestBodyAnalysis.getGitName()+ "/sonar-project.properties"));
            writer.write("sonar.projectKey=" + requestBodyAnalysis.getGitName() + System.lineSeparator());
            writer.append("sonar.projectName=" + requestBodyAnalysis.getGitName() + System.lineSeparator());
            writer.append("sonar.sourceEncoding=UTF-8" + System.lineSeparator());
            writer.append("sonar.sources=." + System.lineSeparator());
            writer.append("sonar.java.binaries=." + System.lineSeparator());
            writer.close();
	        
	        //start analysis
	        ProcessBuilder pbuilder2 = new ProcessBuilder("bash", "-c", "cd /tmp/" + requestBodyAnalysis.getGitName()+
	        			"; /sonar-scanner-4.6.2.2472-linux/bin/sonar-scanner");
	        File err2 = new File("err2.txt");
	        pbuilder2.redirectError(err2);
	        Process p2 = pbuilder2.start();
	        BufferedReader reader2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
	        String line2;
	        System.out.println("start analysis");
	        while ((line2 = reader2.readLine()) != null) {
	            System.out.println(line2);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get 2 metrics from SonarQube API
	 * @param projectKey the project key
	 * @return
	 */
	public Metric[] getMeasures(String projectKey) {
		try {
			Metric[] metrics= new Metric[2];
            URL url = new URL(SmartclideTdPrincipalApplication.SonarQubeURL + "/api/measures/component?component="+projectKey+
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

}
