package gr.nikos.smartclideTDPrincipal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartclideTdPrincipalApplication {

	public static String SonarQubeURL;
	
	public static void main(String[] args) {
		if(args.length==1) {
			SonarQubeURL= args[0];
			SpringApplication.run(SmartclideTdPrincipalApplication.class, args);
		}
		else {
			System.err.println("Arg for SonarQube instance is required!");
		}
	}

}
