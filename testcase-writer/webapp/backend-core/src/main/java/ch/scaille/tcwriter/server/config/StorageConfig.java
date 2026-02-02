package ch.scaille.tcwriter.server.config;

import java.nio.file.Path;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.scaille.tcwriter.persistence.ConfigDao;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.persistence.ModelDao;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;
import ch.scaille.tcwriter.server.dao.DictionaryDao;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;
import ch.scaille.tcwriter.server.dao.ITestCaseDao;
import ch.scaille.tcwriter.server.dao.TestCaseDao;
import ch.scaille.util.persistence.DaoFactory;

@Configuration
public class StorageConfig {

	@Bean
	DaoFactory daoFactory(@Value("${app.dataFolder:#{systemProperties['user.home'] + '/.var/lib/tcwriter/data'}}") Path dataFolder) {
		return DaoFactory.cpPlus(Set.of(DaoConfigs.USER_RESOURCES), new DaoFactory.FsDsFactory(dataFolder));
	}

	@Bean
	ConfigDao configDao(DaoFactory daoFactory) {
		return new ConfigDao(daoFactory, ".", ConfigDao.defaultDataHandlers())
				.setConfiguration("server");
	}

	@Bean
	IModelDao modelDao(DaoFactory daoFactory, ConfigDao configDao) {
		return new ModelDao(daoFactory, configDao.getCurrentConfigProperty(), ModelDao::defaultDataHandlers);
	}

	@Bean
	IDictionaryDao dictionaryDao(IModelDao modelDao) {
		return new DictionaryDao(modelDao);
	}

	@Bean
	ITestCaseDao testCaseDao(IModelDao modelDao) {
		return new TestCaseDao(modelDao);
	}

	@Bean
	ObjectMapper defaultMapper() {
		return new ObjectMapper();
	}

}
