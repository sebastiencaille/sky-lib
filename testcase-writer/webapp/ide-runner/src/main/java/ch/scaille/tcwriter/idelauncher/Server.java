package ch.scaille.tcwriter.idelauncher;

import org.springframework.boot.SpringApplication;

import ch.scaille.tcwriter.server.config.ApplicationConfig;

/**
 * Run with<br>
 * jvm arg: --add-reads org.hibernate.validator=org.apache.tomcat.embed.el --add-opens=java.base/java.lang=spring.core
 */
public class Server {

	static void main(String[] args) {
		// This adds the package of Application.class in Spring. Using Server may cause
		// spring to scan the entire application
		SpringApplication.run(ApplicationConfig.class, args);
	}

}