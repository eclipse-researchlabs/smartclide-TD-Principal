package gr.nikos.smartclideTDPrincipal.controller.entity;

public class RequestBodyEndpoints {

    private String sonarQubeProjectKey;
    private String gitUrl;
    private String gitToken;

    public RequestBodyEndpoints(String sonarQubeProjectKey, String gitUrl, String gitToken) {
        this.sonarQubeProjectKey = sonarQubeProjectKey;
        this.gitUrl = gitUrl;
        this.gitToken = gitToken;
    }

    public String getSonarQubeProjectKey() {
        return sonarQubeProjectKey;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public String getGitToken() {
        return gitToken;
    }

    public void setSonarQubeProjectKey(String sonarQubeProjectKey) {
        this.sonarQubeProjectKey = sonarQubeProjectKey;
    }

    public void setGitUrl(String folderName) {
        this.gitUrl = folderName;
    }

    public void setGitToken(String gitToken) {
        this.gitToken = gitToken;
    }

}
