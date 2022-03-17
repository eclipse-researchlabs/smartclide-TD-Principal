package gr.nikos.smartclideTDPrincipal.Analysis;

import java.util.List;

public class Report {
    private Metric metrics;
    private List<Issue> issueList;

    public Report(Metric metrics, List<Issue> issueList) {
        this.metrics = metrics;
        this.issueList = issueList;
    }

    public Metric getMetrics() {
        return metrics;
    }

    public List<Issue> getIssueList() {
        return issueList;
    }

    public void setMetrics(Metric metrics) {
        this.metrics = metrics;
    }

    public void setIssueList(List<Issue> issueList) {
        this.issueList = issueList;
    }
}
