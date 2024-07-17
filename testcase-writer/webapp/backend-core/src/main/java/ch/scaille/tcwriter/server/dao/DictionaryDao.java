package ch.scaille.tcwriter.server.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.persistence.IModelDao;

public class DictionaryDao extends AbstractDao implements IDictionaryDao {

    private final Map<String, TestDictionary> cache = new ConcurrentHashMap<>();

    private final IModelDao modelDao;

    public DictionaryDao(IModelDao modelDao) {
        this.modelDao = modelDao;
    }

    @Override
    public List<Metadata> listAll() {
        return modelDao.listDictionaries();
    }

    @Override
    public Optional<TestDictionary> load(String dictionaryName) {
        return cacheIfPresent(cache, dictionaryName,
                () -> modelDao.readTestDictionary(dictionaryName));
    }

}
