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
