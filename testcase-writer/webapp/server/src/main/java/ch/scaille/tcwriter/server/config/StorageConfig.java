package ch.scaille.tcwriter.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.scaille.tcwriter.server.dao.ContextDao;
import ch.scaille.tcwriter.server.dao.DictionaryDao;
import ch.scaille.tcwriter.server.dao.DictionaryFsDao;
import ch.scaille.tcwriter.server.dao.InMemoryContextDao;
import ch.scaille.tcwriter.server.dao.TestCaseDao;
import ch.scaille.tcwriter.server.dao.TestCaseFsDao;

@Configuration
public class StorageConfig {

	@Bean
	public DictionaryDao dictionaryDao() {
		return new DictionaryFsDao();
	}

	@Bean
	public TestCaseDao testCaseDao() {
		return new TestCaseFsDao();
	}

	@Bean
	public ContextDao inMemoryContextDao() {
		return new InMemoryContextDao();
	}

}
