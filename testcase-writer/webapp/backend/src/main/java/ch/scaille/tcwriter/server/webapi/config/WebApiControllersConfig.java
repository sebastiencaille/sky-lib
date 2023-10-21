package ch.scaille.tcwriter.server.webapi.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import ch.scaille.tcwriter.server.dao.ContextDao;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.facade.DictionaryFacade;
import ch.scaille.tcwriter.server.facade.TestCaseFacade;
import ch.scaille.tcwriter.server.webapi.controllers.ContextController;
import ch.scaille.tcwriter.server.webapi.controllers.DictionaryController;
import ch.scaille.tcwriter.server.webapi.controllers.TestCaseController;

@Configuration
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
public class WebApiControllersConfig {

	@Bean
	RequestMappingHandlerMapping requestMappingHandlerMapping(CorsConfigurationSource corsSource) {
		final var requestMappingHandlerMapping = new RequestMappingHandlerMapping();
		requestMappingHandlerMapping.setOrder(0);
		requestMappingHandlerMapping.setCorsConfigurationSource(corsSource);
		return requestMappingHandlerMapping;
	}

	@Bean
	ContextController contextController(ContextFacade contextFacade, ContextDao contextDao,
			NativeWebRequest nativeWebRequest) {
		return new ContextController(contextFacade, contextDao, nativeWebRequest);
	}

	@Bean
	DictionaryController dictionariesController(ContextFacade contextFacade, DictionaryFacade dictionaryFacade,
			NativeWebRequest nativeWebRequest) {
		return new DictionaryController(contextFacade, dictionaryFacade, nativeWebRequest);
	}

	@Bean
	TestCaseController testCaseController(ContextFacade contextFacade, TestCaseFacade testCaseFacade,
			NativeWebRequest nativeWebRequest, MessageSendingOperations<String> feedbackSendingTemplate) {
		return new TestCaseController(contextFacade, testCaseFacade, feedbackSendingTemplate, nativeWebRequest);
	}

}
