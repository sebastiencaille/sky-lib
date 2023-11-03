package ch.scaille.tcwriter.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;
import ch.scaille.tcwriter.server.dao.ITestCaseDao;
import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.facade.ClusteredSessionFacade;
import ch.scaille.tcwriter.server.facade.ClusteredSessionFacadeImpl;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.facade.ContextFacadeImpl;
import ch.scaille.tcwriter.server.facade.DictionaryFacade;
import ch.scaille.tcwriter.server.facade.TestCaseFacade;
import ch.scaille.tcwriter.server.repository.ClusteredSessionRepository;
import ch.scaille.tcwriter.services.testexec.JUnitTestExecutor;
import ch.scaille.util.helpers.ClassLoaderHelper;
import jakarta.persistence.EntityManagerFactory;

@Configuration
public class ServiceConfig {

	@Bean
	DictionaryFacade dictionaryFacade(IDictionaryDao dictionaryDao) {
		return new DictionaryFacade(dictionaryDao);
	}

	@Bean
	TestCaseFacade testCaseFacade(IDictionaryDao dictionaryDao, ITestCaseDao testCaseDao,
			JUnitTestExecutor junitExecution) {
		return new TestCaseFacade(dictionaryDao, testCaseDao, junitExecution);
	}

	@Bean
	ContextFacade contextFacade() {
		return new ContextFacadeImpl();
	}

	@Bean
	ClusteredSessionFacade clusteredSessionFacade(EntityManagerFactory emf, ClusteredSessionRepository repository) {
		return new ClusteredSessionFacadeImpl(repository);
	}
	
	@Bean
	JUnitTestExecutor jUnitTestExecutor(IConfigDao configDao, IModelDao modelDao) {
		return new JUnitTestExecutor(configDao, modelDao, ClassLoaderHelper.guessClassPath());
	}

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	Context context() {
		return new Context();
	}
}
