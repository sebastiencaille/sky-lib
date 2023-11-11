package ch.scaille.tcwriter.server;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextClosedEvent;

@SpringBootApplication
@ComponentScan({ "ch.scaille.tcwriter.server.config" })
public class Server {

	public static void main(String[] args) {
		var ctx = SpringApplication.run(Server.class, args);
		System.out.println("Bob");
	}

}