package ch.scaille.tcwriter.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.scaille.tcwriter.server.services.TestCaseService;

@Configuration
public class ServicesConfig {
	
	@Bean
	TestCaseService testCaseService() {
		return new TestCaseService();
	}
	
}
