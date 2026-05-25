package ch.scaille.tcwriter.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import ch.scaille.tcwriter.server.config.ApplicationConfig;

/**
 * Run with<br>
 * Project: testcase-writer-webapp-api-v0-backend<br>
 * jvm arg: --add-modules testcase.writer.webapp.api.v0.backend --add-reads org.hibernate.validator=org.apache.tomcat.embed.el
 */
@EnableAutoConfiguration
public class Server {

	static void main(String[] args) {
		// This adds the package of Application.class in Spring. Using Server may cause
		// spring to scan the entire application
		SpringApplication.run(ApplicationConfig.class, args);
	}

}