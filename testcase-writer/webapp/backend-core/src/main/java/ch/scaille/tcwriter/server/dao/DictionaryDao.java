package ch.scaille.tcwriter.server.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.util.persistence.DaoFactory;
import ch.scaille.util.persistence.StorageException;

public class DictionaryDao extends AbstractDao implements IDictionaryDao {

    private final Map<String, TestDictionary> cache = new ConcurrentHashMap<>();

    private final IModelDao modelDao;

    public DictionaryDao(IModelDao modelDao, DaoFactory.IDataSourceFactory factory) {
        super(factory);
        this.modelDao = modelDao;
    }

    @Override
    public List<Metadata> listAll() {
        return modelDao.listDictionaries();
    }

    @Override
    public Metadata loadMetadata(String locator) {
        return super.loadMetadata(locator,
                        l -> modelDao.readTestDictionary(l)
                                .map(TestDictionary::getMetadata)
                                .orElse(null))
                .orElse(null);
    }

    @Override
    public TestDictionary load(String dictionaryName) {
        return cacheIfPresent(cache, dictionaryName,
                () -> modelDao.readTestDictionary(dictionaryName)).orElse(null);
    }

}
