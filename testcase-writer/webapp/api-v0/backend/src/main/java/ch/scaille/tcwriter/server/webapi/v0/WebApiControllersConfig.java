package ch.scaille.tcwriter.server.webapi.v0;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.SessionRepository;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.facade.DictionaryFacade;
import ch.scaille.tcwriter.server.facade.TestCaseFacade;
import ch.scaille.tcwriter.server.facade.WebFeedbackFacade;
import ch.scaille.tcwriter.server.services.SessionAccessor;
import ch.scaille.tcwriter.server.webapi.v0.controllers.ContextController;
import ch.scaille.tcwriter.server.webapi.v0.controllers.DictionaryController;
import ch.scaille.tcwriter.server.webapi.v0.controllers.TestCaseController;

@Configuration
public class WebApiControllersConfig {

	@Bean
	ContextController contextControllerV0(SessionAccessor sessionAccessor, ContextFacade contextFacade,
			NativeWebRequest nativeWebRequest) {
		return new ContextController(sessionAccessor, contextFacade, nativeWebRequest);
	}

	@Bean
	DictionaryController dictionariesControllerV0(SessionAccessor sessionAccessor, DictionaryFacade dictionaryFacade,
			NativeWebRequest nativeWebRequest) {
		return new DictionaryController(sessionAccessor, dictionaryFacade, nativeWebRequest);
	}

	@Bean
	TestCaseController testCaseControllerV0(SessionAccessor sessionAccessor, SessionRepository<?> sessionRepository,
			TestCaseFacade testCaseFacade, NativeWebRequest nativeWebRequest,
			WebFeedbackFacade webFeedbackFacade) {
		return new TestCaseController(sessionAccessor, testCaseFacade, webFeedbackFacade, nativeWebRequest);
	}

}
