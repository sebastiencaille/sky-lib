package ch.scaille.tcwriter.server.webapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.facade.DictionaryFacade;
import ch.scaille.tcwriter.server.facade.TestCaseFacade;
import ch.scaille.tcwriter.server.webapi.controllers.ContextController;
import ch.scaille.tcwriter.server.webapi.controllers.DictionaryController;
import ch.scaille.tcwriter.server.webapi.controllers.TestCaseController;

@Configuration
public class WebApiControllersConfig {

	@Bean
	ContextController contextController(Context context, ContextFacade contextFacade,
			NativeWebRequest nativeWebRequest) {
		return new ContextController(context, contextFacade, nativeWebRequest);
	}

	@Bean
	DictionaryController dictionariesController(Context context, DictionaryFacade dictionaryFacade,
			NativeWebRequest nativeWebRequest) {
		return new DictionaryController(context, dictionaryFacade, nativeWebRequest);
	}

	@Bean
	TestCaseController testCaseController(Context context, TestCaseFacade testCaseFacade,
			NativeWebRequest nativeWebRequest, MessageSendingOperations<String> feedbackSendingTemplate) {
		return new TestCaseController(context, testCaseFacade, feedbackSendingTemplate, nativeWebRequest);
	}

}
