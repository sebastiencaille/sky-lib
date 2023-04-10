package ch.scaille.tcwriter.server.webapi.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import ch.scaille.tcwriter.config.IConfigManager;
import ch.scaille.tcwriter.server.dao.ContextDao;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;
import ch.scaille.tcwriter.server.dao.ITestCaseDao;
import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.tcwriter.server.services.TestCaseService;
import ch.scaille.tcwriter.server.webapi.controllers.ContextController;
import ch.scaille.tcwriter.server.webapi.controllers.DictionaryController;
import ch.scaille.tcwriter.server.webapi.controllers.TestCaseController;
import ch.scaille.tcwriter.testexec.JUnitTestExecutor;

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
	ContextController contextController(ContextService contextService, ContextDao contextDao,
			NativeWebRequest nativeWebRequest) {
		return new ContextController(contextService, contextDao, nativeWebRequest);
	}

	@Bean
	DictionaryController dictionariesController(ContextService contextService, IDictionaryDao dictionaryDao,
			NativeWebRequest nativeWebRequest) {
		return new DictionaryController(contextService, dictionaryDao, nativeWebRequest);
	}

	@Bean
	TestCaseController testCaseController(JUnitTestExecutor jUnitTestExecutor, ContextService contextService,
			IDictionaryDao dictionaryDao, ITestCaseDao testCaseDao, TestCaseService testCaseService,
			NativeWebRequest nativeWebRequest, MessageSendingOperations<String> feedbackSendingTemplate,
			IConfigManager configManager) {
		return new TestCaseController(jUnitTestExecutor, contextService, dictionaryDao, testCaseDao, testCaseService,
				feedbackSendingTemplate, nativeWebRequest);
	}

}
