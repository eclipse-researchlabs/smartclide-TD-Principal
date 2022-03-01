package gr.nikos.smartclideTDPrincipal.Analysis;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import gr.nikos.smartclideTDPrincipal.Parser.InvestigatorFacade;
import gr.nikos.smartclideTDPrincipal.Parser.infrastructure.entities.MethodCallSet;
import gr.nikos.smartclideTDPrincipal.Parser.infrastructure.entities.MethodDecl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EndpointAnalysisService {

    @Value("${gr.nikos.smartclide.sonarqube.url}")
    private String sonarQubeUrl;

    @Autowired
    private AnalysisService analysisService;

    private HashMap<MethodDeclaration, String> methodsOfStartingEndpoints= new HashMap<>();

    public HashMap<String,Metric> getEnpointMetrics(String url) {
        try {
            HashMap<String,Metric> hashMap=new HashMap<>();

            //Get all issues
            String[] temp= url.split("/");
            String gitName= temp[temp.length-1];
            String projectKey= temp[temp.length-2]+":"+temp[temp.length-1];
            List<Issue> allIssues= analysisService.getIssues(projectKey);

            //clone
            ProcessBuilder pbuilder1 = new ProcessBuilder("bash", "-c", "git clone " + url);
            Process p1 = pbuilder1.start();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            String line1;
            while ((line1 = reader1.readLine()) != null) {
                System.out.println(line1);
            }

            // Get Mappings
            try {
                getMappingsFromAllFiles(new File("/"+gitName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //For each mapping method
            HashMap<MethodDeclaration, List<Issue>> issuesOfEndPoints= new HashMap<>();
            for(MethodDeclaration md: methodsOfStartingEndpoints.keySet()){
                //parse and find call tree
                InvestigatorFacade facade = new InvestigatorFacade("/"+gitName, methodsOfStartingEndpoints.get(md), md);
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
                                    .filter(issue -> issue.getIssueDirectory().replace(projectKey+":", "").equals(methodCall.getFilePath()))
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
                    issuesOfEndPoints.put(md, endpointIssues);
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

                    String annotationPath="";
                    for(AnnotationExpr n: md.getAnnotations()){
                        if(n.getName().asString().contains("Mapping")){
                            annotationPath= n.toString();
                        }
                    }

                    hashMap.put(annotationPath+" | "+md.getName().toString(),new Metric("TD", total));
                }
            }

            //delete clone
            FileSystemUtils.deleteRecursively(new File("/"+gitName));

            return  hashMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //For each file find endpoints
    private void getMappingsFromAllFiles(File folder) throws FileNotFoundException, IOException {
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

    private static class MethodNamePrinter extends VoidVisitorAdapter<List<MethodDeclaration>> {
        @Override
        public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
            super.visit(md, collector);
            if(!md.getAnnotationByName("GetMapping").isEmpty() ||
                    !md.getAnnotationByName("PostMapping").isEmpty() ||
                    !md.getAnnotationByName("PutMapping").isEmpty() ||
                    !md.getAnnotationByName("DeleteMapping").isEmpty() ||
                    !md.getAnnotationByName("PatchMapping").isEmpty() ||
                    !md.getAnnotationByName("RequestMapping ").isEmpty() ) {
                collector.add(md);
            }
        }
    }

    private static void printResults(Set<MethodCallSet> results) {
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
