/*******************************************************************************
 * Copyright (C) 2021-2022 UoM - University of Macedonia
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package gr.nikos.smartclideTDPrincipal.model;

import java.util.List;

public class Report {
    private String method;
    private Metric metrics;
    private List<Issue> issueList;

    public Report(String method, Metric metrics, List<Issue> issueList) {
        this.method = method;
        this.metrics = metrics;
        this.issueList = issueList;
    }

    public String getMethod() {
        return method;
    }

    public Metric getMetrics() {
        return metrics;
    }

    public List<Issue> getIssueList() {
        return issueList;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setMetrics(Metric metrics) {
        this.metrics = metrics;
    }

    public void setIssueList(List<Issue> issueList) {
        this.issueList = issueList;
    }
}
