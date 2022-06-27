package gr.nikos.smartclideTDPrincipal.service.analysis;

import java.io.*;

public class SonarAnalysis {

    private Git gitProject;
    private String report="";

    public SonarAnalysis(Git gitProject) {
        this.gitProject = gitProject;
    }

    //Create Sonar Properties file
    public void createSonarFile() throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(gitProject.getLocalPath()+ "/sonar-project.properties"));
            writer.write("sonar.projectKey=" + gitProject.getOwnerName() +":"+ gitProject.getProjectName() + System.lineSeparator());
            writer.append("sonar.projectName=" + gitProject.getOwnerName() +":"+ gitProject.getProjectName() + System.lineSeparator());
            writer.append("sonar.sourceEncoding=UTF-8" + System.lineSeparator());
            writer.append("sonar.sources=." + System.lineSeparator());
            writer.append("sonar.java.binaries=." + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    //Start Analysis with sonar scanner
    public void makeSonarAnalysis() throws IOException {
        ProcessBuilder pbuilder2 = new ProcessBuilder("bash", "-c", "cd " +gitProject.getLocalPath()+
                "; /sonar-scanner-4.6.2.2472-linux/bin/sonar-scanner");
        File err2 = new File("err2.txt");
        pbuilder2.redirectError(err2);
        Process p2 = pbuilder2.start();
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
        String line2;
        System.out.println("start analysis");
        while ((line2 = reader2.readLine()) != null) {
            System.out.println(line2);
            report+="! "+line2;
        }
        if(err2.exists()){
            report+="err2 File; ";
        }
    }

    public String getReport() {
        return report;
    }
}
