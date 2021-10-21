package gr.nikos.smartclideTDPrincipal.Analysis;

public class Issue {

	private String issueRule;
    private String issueName;
    private String issueSeverity;
    private String issueDebt;
    private String issueType;
    private String issueDirectory;
    private String issueStartLine;
    private String issueEndLine;
    
	public Issue(String issueRule, String issueName, String issueSeverity, String issueDebt, String issueType,
			String issueDirectory, String issueStartLine, String issueEndLine) {
		super();
		this.issueRule = issueRule;
		this.issueName = issueName;
		this.issueSeverity = issueSeverity;
		this.issueDebt = issueDebt;
		this.issueType = issueType;
		this.issueDirectory = issueDirectory;
		this.issueStartLine = issueStartLine;
		this.issueEndLine = issueEndLine;
	}

	public String getIssueRule() {
		return issueRule;
	}

	public void setIssueRule(String issueRule) {
		this.issueRule = issueRule;
	}

	public String getIssueName() {
		return issueName;
	}

	public void setIssueName(String issueName) {
		this.issueName = issueName;
	}

	public String getIssueSeverity() {
		return issueSeverity;
	}

	public void setIssueSeverity(String issueSeverity) {
		this.issueSeverity = issueSeverity;
	}

	public String getIssueDebt() {
		return issueDebt;
	}

	public void setIssueDebt(String issueDebt) {
		this.issueDebt = issueDebt;
	}

	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public String getIssueDirectory() {
		return issueDirectory;
	}

	public void setIssueDirectory(String issueDirectory) {
		this.issueDirectory = issueDirectory;
	}

	public String getIssueStartLine() {
		return issueStartLine;
	}

	public void setIssueStartLine(String issueStartLine) {
		this.issueStartLine = issueStartLine;
	}

	public String getIssueEndLine() {
		return issueEndLine;
	}

	public void setIssueEndLine(String issueEndLine) {
		this.issueEndLine = issueEndLine;
	}
    
}
