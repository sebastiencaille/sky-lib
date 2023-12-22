package ch.scaille.tcwriter.server.webapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.session.SessionRepository;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.facade.DictionaryFacade;
import ch.scaille.tcwriter.server.facade.TestCaseFacade;
import ch.scaille.tcwriter.server.webapi.v0.controllers.ContextController;
import ch.scaille.tcwriter.server.webapi.v0.controllers.DictionaryController;
import ch.scaille.tcwriter.server.webapi.v0.controllers.TestCaseController;

@Configuration
public class WebApiControllersConfig {


	@Bean
	ContextController contextControllerV0(Context context, ContextFacade contextFacade,
			NativeWebRequest nativeWebRequest) {
		return new ContextController(context, contextFacade, nativeWebRequest);
	}

	@Bean
	DictionaryController dictionariesControllerV0(Context context, DictionaryFacade dictionaryFacade,
			NativeWebRequest nativeWebRequest) {
		return new DictionaryController(context, dictionaryFacade, nativeWebRequest);
	}

	@Bean
	TestCaseController testCaseControllerV0(Context context, SessionRepository<?> sessionRepository,
			TestCaseFacade testCaseFacade, NativeWebRequest nativeWebRequest,
			SimpMessageSendingOperations feedbackSendingTemplate) {
		return new TestCaseController(context, sessionRepository, testCaseFacade, feedbackSendingTemplate,
				nativeWebRequest);
	}


	@Bean
	@DependsOn("webApiServlet")
	RequestMappingHandlerMapping webApiRequestMappingHandlerMapping() {
		var requestMappingHandlerMapping = new RequestMappingHandlerMapping();
		requestMappingHandlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return requestMappingHandlerMapping;
	}
}
