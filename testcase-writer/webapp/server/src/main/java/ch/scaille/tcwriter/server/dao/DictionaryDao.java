package ch.scaille.tcwriter.server.dao;

import static ch.scaille.util.helpers.LambdaExt.raise;
import static ch.scaille.util.helpers.LambdaExt.uncheckF;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.util.exceptions.StorageRTException;

public class DictionaryDao implements IDictionaryDao {

	private final Map<String, TestDictionary> cache = new ConcurrentHashMap<>();

	private final IModelDao modelDao;

	public DictionaryDao(IModelDao modelDao) {
		this.modelDao = modelDao;
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
