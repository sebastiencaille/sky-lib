package ch.scaille.tcwriter.server.config;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

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
	public DaoFactory daoFactory(@Value("${app.dataFolder:/var/lib/tcwriter/data}") Path dataFolder) {
		return DaoFactory.defaultsWith(new DaoFactory.FsDsFactory(dataFolder));
	}

	@Bean
	public ConfigDao configDao(DaoFactory daoFactory) {
		return new ConfigDao(daoFactory, ".", ConfigDao.defaultDataHandlers())
				.setConfiguration("server");
	}

	@Bean
	public IModelDao modelDao(DaoFactory daoFactory, ConfigDao configDao) {
		return new ModelDao(daoFactory, configDao.getCurrentConfigProperty(), ModelDao.defaultDataHandlers());
	}

	@Bean
	public IDictionaryDao dictionaryDao(IModelDao modelDao) {
		return new DictionaryDao(modelDao);
	}

	@Bean
	public ITestCaseDao testCaseDao(IModelDao modelDao) {
		return new TestCaseDao(modelDao);
	}

	@Bean
	ObjectMapper defaultMapper() {
		final var mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		return mapper;
	}

}
