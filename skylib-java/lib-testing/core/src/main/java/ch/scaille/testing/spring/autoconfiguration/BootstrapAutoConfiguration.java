package ch.scaille.testing.spring.autoconfiguration;

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
	BeanFactoryPostProcessor bootstrap(
			@Value("${app.dataFolder:#{systemProperties['user.home'] + '/.var/lib/tcwriter/data'}}") Path dataFolder) {
		final var services = ServiceLoader.load(TestBootstrapService.class).stream().toList();
		log.info(() -> "Bootstrapping " + services.size() + " services");
		return _ -> services.forEach(bootstrap -> bootstrap.get().bootstrap(dataFolder));
	}

}
