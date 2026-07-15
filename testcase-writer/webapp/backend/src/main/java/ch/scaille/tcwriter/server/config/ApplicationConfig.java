package ch.scaille.tcwriter.server.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("defaults.properties")
public class ApplicationConfig {
	// for SpringBootApplication
}
