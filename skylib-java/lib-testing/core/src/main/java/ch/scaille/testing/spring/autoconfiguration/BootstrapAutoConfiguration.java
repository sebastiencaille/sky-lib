package ch.scaille.testing.spring.autoconfiguration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ServiceLoader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import ch.scaille.testing.api.TestBootstrapService;
import lombok.extern.java.Log;

@AutoConfiguration
@Log
public class BootstrapAutoConfiguration {

	@Bean
	static BeanFactoryPostProcessor bootstrap(
			@Value("${app.dataFolder:#{systemProperties['user.home'] + '/.var/lib/tcwriter/data'}}") Path dataFolder)
			throws IOException {
		
		final var services = ServiceLoader.load(TestBootstrapService.class).stream().toList();
		log.info(() -> "Bootstrapping " + services.size() + " services");
		return _ -> services.stream().forEach(bootstrap -> bootstrap.get().bootstrap(dataFolder));
	}

}
