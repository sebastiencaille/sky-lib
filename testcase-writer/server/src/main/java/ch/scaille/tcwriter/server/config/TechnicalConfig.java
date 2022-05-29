package ch.scaille.tcwriter.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.scaille.tcwriter.server.dao.ContextDao;
import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.tcwriter.server.services.ContextServiceImpl;

@Configuration
public class TechnicalConfig {

	@Bean
	public ContextService contextService(ContextDao contextDao) {
		return new  ContextServiceImpl(contextDao);
	}
	
}
