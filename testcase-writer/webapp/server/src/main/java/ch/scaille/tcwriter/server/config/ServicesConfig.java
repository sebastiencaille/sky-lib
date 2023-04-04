package ch.scaille.tcwriter.server.config;

import ch.scaille.tcwriter.config.IConfigManager;
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
	JUnitTestExecutor jUnitTestExecutor(IConfigManager configManager, IModelDao modelDao) {
		return new JUnitTestExecutor(configManager, modelDao, ClassLoaderHelper.guessClassPath());
	}

}
