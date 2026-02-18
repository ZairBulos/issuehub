package com.issuehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

@SpringBootApplication
@Modulithic(
		systemName = "IssueHub",
		sharedModules = { "shared" }
)
public class IssueHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(IssueHubApplication.class, args);
	}

}
