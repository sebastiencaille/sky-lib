package ch.scaille.tcwriter.server.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import ch.scaille.tcwriter.javatc.testexec.JUnitTestExecutor;
import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;
import ch.scaille.tcwriter.server.dao.ITestCaseDao;
import ch.scaille.tcwriter.server.facade.DictionaryFacade;
import ch.scaille.tcwriter.server.facade.TestCaseFacade;
import ch.scaille.tcwriter.server.services.SessionManager;
import ch.scaille.tcwriter.server.services.SessionManagerImpl;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
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
	JUnitTestExecutor jUnitTestExecutor(IConfigDao configDao, IModelDao modelDao,
			org.springframework.context.ApplicationContext context,
			@Value("${tcwriter.javatc-resources:}") String tcResource) {
		Path resourcesFolder; 
		try {
			if (tcResource.length() > 0) {
				resourcesFolder = Paths.get(tcResource);
				if (!Files.isDirectory(resourcesFolder)) {
					throw new IllegalStateException("No javatc resources in " + resourcesFolder.toAbsolutePath());
				}
			} else {
				final var url = context.getResource("javatc-resources");
				if (url.exists()) {
					resourcesFolder = url.getFilePath();
				} else {
					throw new IllegalStateException("No resource javatc-resources found on classpath");
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("", e);
		}
		log.info("Using tcwriter-resources {}", resourcesFolder);
		return new JUnitTestExecutor(configDao, modelDao, resourcesFolder);
	}

	@Bean
	SessionManager sessionAccessor() {
		return new SessionManagerImpl();
	}

}
