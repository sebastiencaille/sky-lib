package ch.scaille.tcwriter.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.scaille.tcwriter.server.dao.ContextDao;
import ch.scaille.tcwriter.server.dao.DictionaryDao;
import ch.scaille.tcwriter.server.dao.FsDictionaryDao;
import ch.scaille.tcwriter.server.dao.InMemoryContextDao;

@Configuration
public class StorageConfig {

	@Bean
	public DictionaryDao dictionaryDao() {
		return new FsDictionaryDao();
	}

	@Bean
	public ContextDao inMemoryContextDao() {
		return new InMemoryContextDao();
	}

}
