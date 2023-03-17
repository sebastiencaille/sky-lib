package ch.scaille.tcwriter.server.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.util.exceptions.StorageRTException;

public class DictionaryDao extends AbstractDao implements IDictionaryDao {

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
    public Optional<TestDictionary> load(String dictionaryName) {
        return cacheIfPresent(cache, dictionaryName,
                () -> modelDao.readTestDictionary(dictionaryName));
    }

}
