package ch.scaille.tcwriter.server.dao;

import static ch.scaille.util.helpers.LambdaExt.raise;
import static ch.scaille.util.helpers.LambdaExt.uncheckF;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;

import ch.scaille.tcwriter.generators.TCConfig;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.util.exceptions.StorageRTException;
import jakarta.annotation.PostConstruct;

public class DictionaryFsDao implements DictionaryDao {

	private final Map<String, TestDictionary> cache = new ConcurrentHashMap<>();

	@Value("${app.dataFolder:/var/lib/tcwriter/data}")
	private String dataFolder;

	private FsModelDao modelDao;

	@PostConstruct
	private void created() {
		final var config = new TCConfig();
		config.setName("server");
		config.setBase(dataFolder);
		modelDao = new FsModelDao(config);
	}

	@Override
	public List<Metadata> listAll() {
		try {
			return modelDao.listDictionaries();
		} catch (IOException e) {
			throw new StorageRTException("Unable to list dictionaries", e);
		}
	}

	@Override
	public TestDictionary load(String dictionaryName) {
		return cache.computeIfAbsent(dictionaryName, uncheckF(modelDao::readTestDictionary,
				raise((t, e) -> new StorageRTException("bad.dictionary.folder", e))));
	}

}
