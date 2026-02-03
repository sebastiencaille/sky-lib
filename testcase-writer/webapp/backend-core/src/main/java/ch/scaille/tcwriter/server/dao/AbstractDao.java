package ch.scaille.tcwriter.server.dao;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.persistence.DaoFactory;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.StorageException;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import tools.jackson.databind.ObjectMapper;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class AbstractDao {

    private static final StorageDataHandlerRegistry REGISTRY =
            new StorageDataHandlerRegistry(new JsonStorageHandler(new ObjectMapper()));

    private final IDao<Map> metadataDao;
    private final Map<String, Metadata> metadataMap = new HashMap<>();

    public AbstractDao(DaoFactory.IDataSourceFactory factory) {
        this.metadataDao = factory.create(Map.class, this.getClass().getSimpleName(), REGISTRY);
        try {
            metadataMap.putAll(metadataDao.load(""));
        } catch (StorageException e) {
            // ignore;
        }
    }

    public synchronized Optional<Metadata> putInCache(String locator, Metadata metadata) throws StorageException {
        if (metadata == null) {
            return Optional.empty();
        }
        metadataMap.put(locator, metadata);
        metadataDao.saveOrUpdate("", (Map) metadataMap);
        return Optional.of(metadata);
    }

    protected Optional<Metadata> loadMetadata(String locator, Function<String, Metadata> loader) {
        try {
            final var cached = metadataMap.get(locator);
            if (cached != null) {
                return Optional.of(cached);
            }
            return putInCache(locator, loader.apply(locator));
        } catch (StorageException e) {
            Logs.of(this).warning("Unable to save cache: " + e.getMessage());
            return Optional.empty();
        }
    }

    protected <T> Optional<T> cacheIfPresent(Map<String, T> cache, String key, Supplier<Optional<T>> loader) {
        final var cached = cache.get(key);
        if (cached != null) {
            return Optional.of(cached);
        }
        final var loaded = loader.get();
        loaded.ifPresent(v -> cache.put(key, v));
        return loaded;
    }

}
