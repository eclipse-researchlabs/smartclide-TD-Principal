package gr.nikos.smartclideTDPrincipal.Analysis;

public class RequestBodyAnalysis {
	
	private String gitURL;
	private String token;
	
	public RequestBodyAnalysis(String gitURL) {
		this.gitURL = gitURL;
	}

	public RequestBodyAnalysis(String gitURL, String token) {
		this.gitURL = gitURL;
		this.token = token;
	}

	public String getGitName() {
		String[] temp= this.gitURL.split("/");
		return temp[temp.length-1];
	}
	
	public String getGitURL() {
		return gitURL;
	}

	public String getToken() {
		return token;
	}

}
