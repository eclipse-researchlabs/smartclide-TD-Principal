package gr.nikos.smartclideTDPrincipal.Analysis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path= "api/analysis")
public class AnalysisController {
	
	@Autowired
	private AnalysisService analysisService;
	
	@GetMapping(path="{projectKey}/measures")
	public Metric[] getMeasures(@PathVariable(value = "projectKey") String projectKey) {
		return analysisService.getMeasures(projectKey);
	}

	@GetMapping(path="{projectKey}/issues")
	public List<Issue> getIssues(@PathVariable(value = "projectKey") String projectKey) {
		return analysisService.getIssues(projectKey);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> makeNewAnalysis(@RequestBody RequestBodyAnalysis requestBodyAnalysis) {
		analysisService.startNewAnalysis(requestBodyAnalysis);
		return new ResponseEntity<>("finished successful", HttpStatus.OK);
	}

}
