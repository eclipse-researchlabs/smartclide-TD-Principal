package gr.nikos.smartclideTDPrincipal.controller;

/*
 * Copyright (C) 2021 UoM - University of Macedonia
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 */

import java.util.List;

import gr.nikos.smartclideTDPrincipal.controller.entity.RequestBodyAnalysis;
import gr.nikos.smartclideTDPrincipal.controller.entity.RequestBodyEndpoints;
import gr.nikos.smartclideTDPrincipal.controller.entity.RequestBodyEndpointsManual;
import gr.nikos.smartclideTDPrincipal.model.Issue;
import gr.nikos.smartclideTDPrincipal.model.Metric;
import gr.nikos.smartclideTDPrincipal.model.Report;
import gr.nikos.smartclideTDPrincipal.service.AnalysisService;
import gr.nikos.smartclideTDPrincipal.service.EndpointAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path= "api/analysis")
public class AnalysisController {
	
	@Autowired
	private AnalysisService analysisService;

	@Autowired
	private EndpointAnalysisService endpointAnalysisService;

	@CrossOrigin(origins = "*")
	@GetMapping(path="{projectKey}/measures")
	public Metric[] getMeasures(@PathVariable(value = "projectKey") String projectKey) {
		return analysisService.getMeasures(projectKey);
	}

	@CrossOrigin(origins = "*")
	@GetMapping(path="{projectKey}/issues")
	public List<Issue> getIssues(@PathVariable(value = "projectKey") String projectKey) {
		return analysisService.getIssues(projectKey);
	}

	@CrossOrigin(origins = "*")
	@GetMapping(path="endpoints")
	public List<Report> getEndpointMetrics(@RequestParam(required = true) String url) {
		return endpointAnalysisService.getEnpointMetrics(url);
	}

	@CrossOrigin(origins = "*")
	@PostMapping(path="endpoints")
	public List<Report> getEndpointMetricsPrivateManual(@RequestBody RequestBodyEndpointsManual requestBodyEndpointsManual) {
		return endpointAnalysisService.getEndpointMetricsPrivateManual(requestBodyEndpointsManual);
	}

	@CrossOrigin(origins = "*")
	@PostMapping(path="endpoints/auto")
	public List<Report> getEndpointMetricsPrivateAuto(@RequestBody RequestBodyEndpoints requestBodyEndpoints) {
		return endpointAnalysisService.getEndpointMetricsPrivateAuto(requestBodyEndpoints);
	}

//	@CrossOrigin(origins = "*")
//	@PostMapping(path="endpoints")
//	public HashMap<String,Report> getEndpointMetricsLocalGitlabSonar(@RequestBody RequestBodyEndpoints requestBodyEndpoints) {
//		return endpointAnalysisService.getEnpointMetricsLocal(requestBodyEndpoints);
//	}

	@CrossOrigin(origins = "*")
	@PostMapping
	public String makeNewAnalysis(@RequestBody RequestBodyAnalysis requestBodyAnalysis) {
		String report = analysisService.startNewAnalysis(requestBodyAnalysis);
		return report;
	}

}
