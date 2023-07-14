package ch.scaille.tcwriter.server.config;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.tcwriter.model.persistence.fsconfig.FsConfigDao;
import ch.scaille.tcwriter.model.persistence.fsmodel.FsModelDao;
import ch.scaille.tcwriter.server.dao.ContextDao;
import ch.scaille.tcwriter.server.dao.DictionaryDao;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;
import ch.scaille.tcwriter.server.dao.ITestCaseDao;
import ch.scaille.tcwriter.server.dao.InMemoryContextDao;
import ch.scaille.tcwriter.server.dao.TestCaseDao;

@Configuration
public class StorageConfig {

	@Value("${app.dataFolder:/var/lib/tcwriter/data}")
	private Path dataFolder;

	@Bean
	public FsConfigDao fsconfigDao() {
		return new FsConfigDao(dataFolder).setConfiguration("server");
	}

	@Bean
	public IModelDao modelDao(FsConfigDao configDao) {
		return new FsModelDao(configDao);
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
	public ContextDao inMemoryContextDao() {
		return new InMemoryContextDao();
	}

}
