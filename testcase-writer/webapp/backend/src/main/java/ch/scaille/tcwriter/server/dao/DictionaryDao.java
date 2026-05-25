package ch.scaille.tcwriter.server.dao;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.persistence.IModelDao;
import org.jspecify.annotations.Nullable;

public class DictionaryDao extends AbstractDao implements IDictionaryDao {

    private final Map<String, TestDictionary> cache = new ConcurrentHashMap<>();

    public DictionaryDao(IModelDao modelDao) {
        super(modelDao);
    }
    @Override
    public List<Metadata> listAll(@Nullable Metadata testMetadata) {
        return modelDao.listDictionaries(testMetadata);
    }

    @Override
    @Nullable
    public Metadata loadMetadata(String identifier) {
        return modelDao.loadDictionaryMetadata(identifier);
    }

    @Override
    @Nullable
    public TestDictionary load(String dictionaryName) {
        return cacheIfPresent(cache, dictionaryName,
                () -> modelDao.readTestDictionary(dictionaryName)).orElse(null);
    }

}
