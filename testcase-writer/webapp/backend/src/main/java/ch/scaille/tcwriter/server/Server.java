package ch.scaille.tcwriter.server;

import org.springframework.boot.SpringApplication;

import ch.scaille.tcwriter.server.config.Application;

public class Server {

	public static void main(String[] args) {
		// This adds the package of Application.class in Spring. Using Server may cause
		// spring to scan the entire application
		SpringApplication.run(Application.class, args);
	}

}