/*******************************************************************************
 * Copyright (C) 2021-2022 UoM - University of Macedonia
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package gr.nikos.smartclideTDPrincipal.service.analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Git {
    private String URL;
    private String token;
    private String ownerName;
    private String projectName;
    private String localPath;
    private String localParentPath;
    private String report="";

    public Git(String URL, String token) {
        this.URL = URL;
        this.token = token;

        String[] temp= URL.replace(".git","").split("/");
        this.ownerName = temp[temp.length-2];
        this.projectName = temp[temp.length-1];
        this.localParentPath = "tmp" + System.currentTimeMillis();
        this.localPath = "/" +this.localParentPath+ "/" + this.projectName;
    }

    public void cloneGit() throws IOException {
        //mkdir
        ProcessBuilder pbuilder = new ProcessBuilder("bash", "-c", "mkdir -p "+this.localParentPath);
        Process p = pbuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // Git Url
        Boolean httpsGit= this.URL.contains("https://");
        String gitUrl= this.URL.replace("https://","").replace("http://","").replace(".git","");
        String url= "https://oauth2:" + this.token + "@" + gitUrl;
        if(!httpsGit){
            url=url.replace("https://","http://");
        }

        // Git configuration
        System.out.println("Git configuration");
        ProcessBuilder pbuilderGit = new ProcessBuilder("bash", "-c", "git config --global http.sslverify \"false\"");
        Process pGit = pbuilderGit.start();
        BufferedReader inputReaderGit = new BufferedReader(new InputStreamReader(pGit.getInputStream()));
        String inputLineGit;
        while ((inputLineGit = inputReaderGit.readLine()) != null) {
            System.out.println("! " + inputLineGit);
        }

        // Clone
        report+="url:"+url+";  ";
        System.out.println("Clone");
        ProcessBuilder pbuilder1 = new ProcessBuilder("bash", "-c", "cd " +this.localParentPath + "; git clone " + url);
        Process p1 = pbuilder1.start();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(p1.getErrorStream()));
        String errorLine;
        while ((errorLine = errorReader.readLine()) != null) {
            System.out.println("~ " + errorLine);
            report+="~ "+errorLine;
        }
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
        String inputLine;
        while ((inputLine = inputReader.readLine()) != null) {
            System.out.println("! " + inputLine);
            report+="! "+inputLine;
        }
    }

    public String getReport() {
        return report;
    }

    public String getLocalParentPath() {
        return localParentPath;
    }

    public String getURL() {
        return URL;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getToken() {
        return token;
    }

    public String getLocalPath() {
        return localPath;
    }
}
