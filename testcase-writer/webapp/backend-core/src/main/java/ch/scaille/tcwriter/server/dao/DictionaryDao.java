package ch.scaille.tcwriter.server.dao;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.util.persistence.DaoFactory;

public class DictionaryDao extends AbstractDao implements IDictionaryDao {

    private final Map<String, TestDictionary> cache = new ConcurrentHashMap<>();

    public DictionaryDao(IModelDao modelDao) {
        super(modelDao);
    }
    @Override
    public List<Metadata> listAll(Metadata testMetadata) {
        return modelDao.listDictionaries(testMetadata);
    }

    @Override
    public Metadata loadMetadata(String locator) {
        return modelDao.loadDictionaryMetadata(locator);
    }

    @Override
    public TestDictionary load(String dictionaryName) {
        return cacheIfPresent(cache, dictionaryName,
                () -> modelDao.readTestDictionary(dictionaryName)).orElse(null);
    }

}
