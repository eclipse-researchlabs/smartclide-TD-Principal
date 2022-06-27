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

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import gr.nikos.smartclideTDPrincipal.controller.entity.RequestBodyEachEndpoint;
import gr.nikos.smartclideTDPrincipal.model.Issue;
import gr.nikos.smartclideTDPrincipal.model.Metric;
import gr.nikos.smartclideTDPrincipal.model.Report;
import gr.nikos.smartclideTDPrincipal.service.analysis.Parser.InvestigatorFacade;
import gr.nikos.smartclideTDPrincipal.service.analysis.Parser.infrastructure.entities.MethodCallSet;
import gr.nikos.smartclideTDPrincipal.service.analysis.Parser.infrastructure.entities.MethodDecl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LocalAnalysis {

    private HashMap<MethodDeclaration, String> methodsOfStartingEndpoints= new HashMap<>();
    private List<File> allJavaFiles = new ArrayList<>();

    public LocalAnalysis() {
    }

    //Get All Java Files
    public void getAllFiles(File folder){
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                getAllFiles(fileEntry);
            }
            else if(fileEntry.getName().endsWith(".java")){
                allJavaFiles.add(fileEntry);
            }
        }
    }

    //Start method tracing and map the issues to endpoints
    public List<Report> getAllReportForAllEndpoints(String dirName, String projectKey, List<Issue> allIssues) {
        System.out.println("Get all Issues");
        //HashMap<String,Report> hashMap=new HashMap<>();
        List<Report> reportList = new ArrayList<>();
        //For each mapping method
        System.out.println("Start method trace");
        for(MethodDeclaration md: methodsOfStartingEndpoints.keySet()){
            //parse and find call tree
            System.out.println("methodsOfStartingEndpoints.get(md):"+methodsOfStartingEndpoints.get(md));
            InvestigatorFacade facade = new InvestigatorFacade(dirName, methodsOfStartingEndpoints.get(md), md);
            Set<MethodCallSet> methodCallSets = facade.start();
            if (!Objects.isNull(methodCallSets)){
                printResults(methodCallSets);
                List<Issue> endpointIssues = new ArrayList<Issue>();

                for (MethodCallSet methodCallSet : methodCallSets) {
                    //For each method called
                    for (MethodDecl methodCall : methodCallSet.getMethodCalls()) {
                        //Get issues in this file
                        List<Issue> filteredList = new ArrayList<Issue>();
                        filteredList = allIssues.stream()
                                .filter(issue -> issue.getIssueDirectory().replace(projectKey +":", "").equals(methodCall.getFilePath()))
                                .collect(Collectors.toList());

                        //For each issue
                        for(Issue issue: filteredList) {
                            int startIssueLine = Integer.parseInt(issue.getIssueStartLine());
                            if(methodCall.getCodeRange().getStartLine() <= startIssueLine &&
                                    methodCall.getCodeRange().getEndLine() >= startIssueLine){
                                endpointIssues.add(issue);
                            }
                        }
                    }
                }

                //Add all issues of endpoint
                int total = 0;
                for(Issue issue: endpointIssues){
                    String debtInString =issue.getIssueDebt();
                    if(debtInString.contains("h")){
                        String hoursInString = debtInString.split("h")[0];
                        int hours = Integer.parseInt(hoursInString) *60;
                        debtInString = hours+"min";
                    }
                    total += Integer.parseInt(debtInString.replace("min", ""));
                }

                /*String annotationPath="";
                for(AnnotationExpr n: md.getAnnotations()){
                    if(n.getName().asString().contains("Mapping")){
                        annotationPath= n.toString();
                    }
                }*/

                //hashMap.put(annotationPath+" | "+md.getName().toString(),new Report(new Metric("TD", total), endpointIssues));
                reportList.add(new Report(md.getName().toString(), new Metric("TD", total), endpointIssues));
            }
        }
        return reportList;
    }

    //For each file find given endpoints
    public void getGivenEndpointsFromAllFiles(List<RequestBodyEachEndpoint> requestBodyEachEndpointList) throws FileNotFoundException, IOException {
        for(RequestBodyEachEndpoint eachEndpoint:requestBodyEachEndpointList) {
            for(File file: allJavaFiles) {
                if (file.getName().equals(eachEndpoint.getFileName()) || file.getName().equals(eachEndpoint.getFileName()+".java")) {
                    List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

                    CompilationUnit cu = StaticJavaParser.parse(file);
                    VoidVisitor<List<MethodDeclaration>> methodNameVisitor = new MethodNamePrinterALL();
                    methodNameVisitor.visit(cu, methods);

                    methods.forEach(n -> {
                        if (n.getDeclarationAsString().replaceAll(" ","").equals(eachEndpoint.getEndpointMethod().replaceAll(" ",""))) {
                            System.out.println(n.getDeclarationAsString());
                            methodsOfStartingEndpoints.put(n, file.getAbsolutePath());
                        }
                    });
                }
            }
        }
    }

    public static class MethodNamePrinterALL extends VoidVisitorAdapter<List<MethodDeclaration>> {
        @Override
        public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
            super.visit(md, collector);
            collector.add(md);
        }
    }

    //For each file find endpoints
    public void getMappingsFromAllFiles(File folder) throws FileNotFoundException, IOException {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                getMappingsFromAllFiles(fileEntry);
            } else if(fileEntry.getName().endsWith(".java")) {
                List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

                CompilationUnit cu = StaticJavaParser.parse(fileEntry);
                VoidVisitor<List<MethodDeclaration>> methodNameVisitor = new MethodNamePrinter();
                methodNameVisitor.visit(cu, methods);

                methods.forEach(n-> System.out.println(n.getDeclarationAsString()));
                methods.forEach(n-> methodsOfStartingEndpoints.put(n, fileEntry.getAbsolutePath()));
            }
        }
    }

    public static class MethodNamePrinter extends VoidVisitorAdapter<List<MethodDeclaration>> {
        @Override
        public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
            super.visit(md, collector);
            if(!md.getAnnotationByName("GetMapping").isEmpty() ||
                    !md.getAnnotationByName("PostMapping").isEmpty() ||
                    !md.getAnnotationByName("PutMapping").isEmpty() ||
                    !md.getAnnotationByName("DeleteMapping").isEmpty() ||
                    !md.getAnnotationByName("PatchMapping").isEmpty() ||
                    !md.getAnnotationByName("RequestMapping").isEmpty() ||
                    !md.getAnnotationByName("GET").isEmpty() ||
                    !md.getAnnotationByName("POST").isEmpty() ||
                    !md.getAnnotationByName("PUT").isEmpty() ||
                    !md.getAnnotationByName("DELETE").isEmpty() ) {
                collector.add(md);
            }
        }
    }

    public static void printResults(Set<MethodCallSet> results) {
        for (MethodCallSet methodCallSet : results) {
            System.out.printf("Methods involved with %s method: %s", methodCallSet.getMethod().getQualifiedName(), System.lineSeparator());
            for (MethodDecl methodCall : methodCallSet.getMethodCalls()) {
                System.out.print(methodCall.getFilePath() + " | " + methodCall.getQualifiedName());
                System.out.printf(" | StartLine: %d | EndLine: %d%s", methodCall.getCodeRange().getStartLine(), methodCall.getCodeRange().getEndLine(), System.lineSeparator());
            }
            System.out.println();
        }
    }
}
