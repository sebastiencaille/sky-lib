package ch.scaille.tcwriter.server.config;

import ch.scaille.tcwriter.model.persistence.IConfigDao;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.tcwriter.testexec.JUnitTestExecutor;
import ch.scaille.util.helpers.ClassLoaderHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.scaille.tcwriter.server.services.TestCaseService;

@Configuration
public class ServicesConfig {
	
	@Bean
	TestCaseService testCaseService() {
		return new TestCaseService();
	}

	@Bean
	JUnitTestExecutor jUnitTestExecutor(IConfigDao configDao, IModelDao modelDao) {
		return new JUnitTestExecutor(configDao, modelDao, ClassLoaderHelper.guessClassPath());
	}

}
