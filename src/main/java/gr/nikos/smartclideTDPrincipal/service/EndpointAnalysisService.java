/*******************************************************************************
 * Copyright (C) 2021-2022 UoM - University of Macedonia
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package gr.nikos.smartclideTDPrincipal.service;

import gr.nikos.smartclideTDPrincipal.service.analysis.Git;
import gr.nikos.smartclideTDPrincipal.service.analysis.LocalAnalysis;
import gr.nikos.smartclideTDPrincipal.controller.entity.RequestBodyEndpoints;
import gr.nikos.smartclideTDPrincipal.controller.entity.RequestBodyEndpointsManual;
import gr.nikos.smartclideTDPrincipal.model.Issue;
import gr.nikos.smartclideTDPrincipal.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.util.*;

@Service
public class EndpointAnalysisService {

    @Value("${gr.nikos.smartclide.sonarqube.url}")
    private String sonarQubeUrl;

    @Autowired
    private AnalysisService analysisService;

    public List<Report> getEndpointMetricsPrivateManual(RequestBodyEndpointsManual requestBodyEndpoints) {
        try {
            String projectKey= requestBodyEndpoints.getSonarQubeProjectKey();

            // Git Clone
            Git gitProject = new Git(requestBodyEndpoints.getGitUrl(), requestBodyEndpoints.getGitToken());
            gitProject.cloneGit();

            // Start new Local Analysis
            LocalAnalysis localAnalysis = new LocalAnalysis();

            // Get All Java Files
            System.out.println("Get all files");
            localAnalysis.getAllFiles(new File(gitProject.getLocalPath()));

            // Get Mappings
            System.out.println("Get all endpoints");
            try {
                localAnalysis.getGivenEndpointsFromAllFiles(requestBodyEndpoints.getRequestBodyEachEndpointList());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Get Report
            List<Issue> allIssues = analysisService.getIssues(projectKey);
            List<Report> reportList= localAnalysis.getAllReportForAllEndpoints(gitProject.getLocalPath(), projectKey, allIssues);

            //delete clone
            FileSystemUtils.deleteRecursively(new File(gitProject.getLocalPath()));

            return reportList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Report> getEndpointMetricsPrivateAuto(RequestBodyEndpoints requestBodyEndpoints) {
        try {
            String projectKey= requestBodyEndpoints.getSonarQubeProjectKey();

            // Git Clone
            Git gitProject = new Git(requestBodyEndpoints.getGitUrl(), requestBodyEndpoints.getGitToken());
            gitProject.cloneGit();

            // Start new Local Analysis
            LocalAnalysis localAnalysis = new LocalAnalysis();

            // Get Mappings
            System.out.println("Get all endpoints");
            try {
                localAnalysis.getMappingsFromAllFiles(new File(gitProject.getLocalPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Get Report
            List<Issue> allIssues = analysisService.getIssues(projectKey);
            List<Report> reportList= localAnalysis.getAllReportForAllEndpoints(gitProject.getLocalPath(), projectKey, allIssues);

            //delete clone
            FileSystemUtils.deleteRecursively(new File(gitProject.getLocalPath()));

            return  reportList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
