package ch.scaille.tcwriter.server.dao;

import static ch.scaille.util.helpers.LambdaExt.raise;
import static ch.scaille.util.helpers.LambdaExt.uncheckF;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import ch.scaille.tcwriter.generators.TCConfig;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.tcwriter.model.testapi.Metadata;
import ch.scaille.tcwriter.model.testapi.TestDictionary;
import ch.scaille.util.exceptions.StorageRTException;

public class FsDictionaryDao implements DictionaryDao {

	private final Map<String, TestDictionary> cache = new HashMap<>();

	@Value("${app.dataFolder:/var/lib/tcwriter/data}")
	private String dataFolder;

	private FsModelDao modelDao;

	private TCConfig config;

	@PostConstruct
	private void created() {
		config = new TCConfig();
		config.setName("server");
		config.setBase(dataFolder);
		modelDao = new FsModelDao(config);
	}

	@Override
	public List<Metadata> listDictionaries() {
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
