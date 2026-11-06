package ch.scaille.tcwriter.server.config;

import java.nio.file.Path;
import java.util.Set;

import ch.scaille.tcwriter.persistence.ModelConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.ObjectMapper;

import ch.scaille.tcwriter.persistence.ConfigDao;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.persistence.ModelDao;
import ch.scaille.tcwriter.persistence.factory.DaoFactory;
import ch.scaille.tcwriter.server.dao.DictionaryDao;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;
import ch.scaille.tcwriter.server.dao.ITestCaseDao;
import ch.scaille.tcwriter.server.dao.TestCaseDao;

@Configuration
public class StorageConfig {

	@Bean
	ch.scaille.util.persistence.DaoFactory.IDataSourceFactory fsDataSource(@Value("${tcwriter.data.folder:#{systemProperties['user.home'] + '/.var/lib/tcwriter/data'}}") Path dataFolder) {
		return new ch.scaille.util.persistence.DaoFactory.FsDsFactory(dataFolder, true);
	}

	@Bean
	ch.scaille.util.persistence.DaoFactory daoFactory(ch.scaille.util.persistence.DaoFactory.IDataSourceFactory fsDataSource) {
		return ch.scaille.util.persistence.DaoFactory.cpPlus(Set.of(DaoFactory.USER_RESOURCES), fsDataSource);
	}

	@Bean
	ConfigDao configDao(ch.scaille.util.persistence.DaoFactory daoFactory) {
		return new ConfigDao(daoFactory, ".", ConfigDao.defaultDataHandlers())
				.setConfiguration("server");
	}

	@Bean
	IModelDao modelDao(ch.scaille.util.persistence.DaoFactory daoFactory, ConfigDao configDao, ch.scaille.util.persistence.DaoFactory.IDataSourceFactory fsDataSource) {
		return new ModelDao(daoFactory, configDao.getSubConfig(ModelConfig.class),
				fsDataSource, ModelDao.defaultDataHandlers());
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
