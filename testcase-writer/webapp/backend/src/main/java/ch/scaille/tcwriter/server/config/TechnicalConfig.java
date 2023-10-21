package ch.scaille.tcwriter.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.scaille.tcwriter.server.dao.ContextDao;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.facade.ContextFacadeImpl;

@Configuration
public class TechnicalConfig {

	@Bean
	public ContextFacade contextService(ContextDao contextDao) {
		return new ContextFacadeImpl(contextDao);
	}


}
