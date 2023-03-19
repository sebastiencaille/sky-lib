package ch.scaille.tcwriter.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication
@ComponentScan("ch.scaille.tcwriter.server.config")
public class Server  {

	public static void main(String[] args) {
		SpringApplication.run(Server.class, args);
	}

}