package ch.scaille.tcwriter.server.bootstrap;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"ch.scaille.tcwriter.server.config"})
public class ExampleServer {

	public static void main(String[] args) throws IOException {
		ExampleBootstrap.main(args);
		SpringApplication.run(ExampleServer.class, args);
	}
	
}
