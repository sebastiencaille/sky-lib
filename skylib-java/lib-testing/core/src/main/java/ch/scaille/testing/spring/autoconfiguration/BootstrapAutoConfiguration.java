package ch.scaille.testing.spring.autoconfiguration;

import java.util.ServiceLoader;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import ch.scaille.testing.api.TestBootstrapService;
import lombok.extern.java.Log;
import org.springframework.core.env.Environment;

@AutoConfiguration
@Log
public class BootstrapAutoConfiguration {

	@Bean
	BeanFactoryPostProcessor bootstrap(Environment environment) {
		final var services = ServiceLoader.load(TestBootstrapService.class).stream().toList();
		log.info(() -> "Bootstrapping " + services.size() + " services");
		return _ -> services.forEach(bootstrap -> bootstrap.get().bootstrap(environment::getRequiredProperty));
	}

}
