package ch.scaille.tcwriter.server.webapi.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import ch.scaille.tcwriter.server.dao.ContextDao;
import ch.scaille.tcwriter.server.dao.DictionaryDao;
import ch.scaille.tcwriter.server.dao.TestCaseDao;
import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.tcwriter.server.webapi.controllers.ContextController;
import ch.scaille.tcwriter.server.webapi.controllers.DictionaryController;
import ch.scaille.tcwriter.server.webapi.controllers.TestCaseController;

@Configuration
public class WebRestControllersConfig {

	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping(CorsConfigurationSource corsSource) {
		var requestMappingHandlerMapping = new RequestMappingHandlerMapping();
		requestMappingHandlerMapping.setOrder(0);
		requestMappingHandlerMapping.setCorsConfigurationSource(corsSource);
		return requestMappingHandlerMapping;
	}

	@Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
		var requestMappingAdapter = new RequestMappingHandlerAdapter();
		requestMappingAdapter.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter()));
		return requestMappingAdapter;
	}

	@Bean
	public ContextController contextController(ContextService contextService, ContextDao contextDao,
			NativeWebRequest nativeWebRequest) {
		return new ContextController(contextService, contextDao, nativeWebRequest);
	}

	@Bean
	public DictionaryController dictionariesController(ContextService contextService, DictionaryDao dictionaryDao,
			NativeWebRequest nativeWebRequest) {
		return new DictionaryController(contextService, dictionaryDao, nativeWebRequest);
	}

	@Bean
	public TestCaseController testCaseController(ContextService contextService, DictionaryDao dictionaryDao,
			NativeWebRequest nativeWebRequest, TestCaseDao testCaseDao) {
		return new TestCaseController(contextService, dictionaryDao, testCaseDao, nativeWebRequest);
	}

}
