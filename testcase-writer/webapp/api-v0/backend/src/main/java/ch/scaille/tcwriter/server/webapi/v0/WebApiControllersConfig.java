package ch.scaille.tcwriter.server.webapi.v0;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.SessionRepository;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.server.facade.DictionaryFacade;
import ch.scaille.tcwriter.server.facade.TestCaseFacade;
import ch.scaille.tcwriter.server.facade.WebFeedbackFacade;
import ch.scaille.tcwriter.server.services.SessionManager;
import ch.scaille.tcwriter.server.webapi.v0.controllers.ContextController;
import ch.scaille.tcwriter.server.webapi.v0.controllers.DictionaryController;
import ch.scaille.tcwriter.server.webapi.v0.controllers.TestCaseController;

@Configuration
public class WebApiControllersConfig {
	
	@Bean
	ContextController contextControllerV0(SessionManager sessionAccessor, NativeWebRequest nativeWebRequest) {
		return new ContextController(sessionAccessor, nativeWebRequest);
	}

	@Bean
	DictionaryController dictionariesControllerV0(DictionaryFacade dictionaryFacade,
			NativeWebRequest nativeWebRequest) {
		return new DictionaryController(dictionaryFacade, nativeWebRequest);
	}

	@Bean
	TestCaseController testCaseControllerV0(SessionManager sessionAccessor, SessionRepository<?> sessionRepository,
			TestCaseFacade testCaseFacade, NativeWebRequest nativeWebRequest, WebFeedbackFacade webFeedbackFacade) {
		return new TestCaseController(sessionAccessor, testCaseFacade, webFeedbackFacade, nativeWebRequest);
	}

}
