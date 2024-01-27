package ch.scaille.tcwriter.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;
import ch.scaille.tcwriter.server.dao.ITestCaseDao;
import ch.scaille.tcwriter.server.facade.DictionaryFacade;
import ch.scaille.tcwriter.server.facade.TestCaseFacade;
import ch.scaille.tcwriter.server.services.SessionAccessor;
import ch.scaille.tcwriter.server.services.SessionManagerImpl;
import ch.scaille.tcwriter.services.testexec.JUnitTestExecutor;
import ch.scaille.util.helpers.ClassLoaderHelper;

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
	JUnitTestExecutor jUnitTestExecutor(IConfigDao configDao, IModelDao modelDao) {
		return new JUnitTestExecutor(configDao, modelDao, ClassLoaderHelper.guessClassPath());
	}

	@Bean
	SessionAccessor sessionAccessor() {
		return new SessionManagerImpl();
	}

}
