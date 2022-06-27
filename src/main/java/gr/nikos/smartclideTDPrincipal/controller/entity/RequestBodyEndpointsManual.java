/*******************************************************************************
 * Copyright (C) 2021-2022 UoM - University of Macedonia
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package gr.nikos.smartclideTDPrincipal.controller.entity;

import java.util.List;

public class RequestBodyEndpointsManual {
    private String sonarQubeProjectKey;
    private String gitUrl;
    private String gitToken;
    private List<RequestBodyEachEndpoint> requestBodyEachEndpointList;

    public RequestBodyEndpointsManual(String sonarQubeProjectKey, String gitUrl, String gitToken, List<RequestBodyEachEndpoint> requestBodyEachEndpointList) {
        this.sonarQubeProjectKey = sonarQubeProjectKey;
        this.gitUrl = gitUrl;
        this.gitToken = gitToken;
        this.requestBodyEachEndpointList= requestBodyEachEndpointList;
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

    public List<RequestBodyEachEndpoint> getRequestBodyEachEndpointList() {
        return requestBodyEachEndpointList;
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

    public void setRequestBodyEachEndpointList(List<RequestBodyEachEndpoint> requestBodyEachEndpointList) {
        this.requestBodyEachEndpointList = requestBodyEachEndpointList;
    }
}
