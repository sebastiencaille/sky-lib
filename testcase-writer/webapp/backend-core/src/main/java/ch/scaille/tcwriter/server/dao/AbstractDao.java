package ch.scaille.tcwriter.server.dao;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.persistence.DaoFactory;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.StorageException;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import lombok.Getter;
import tools.jackson.databind.ObjectMapper;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class AbstractDao {

    @Getter
    private static class MetadataMap {
        private final Map<String, Metadata> metadataMap = new HashMap<>();
    }

    private static final StorageDataHandlerRegistry REGISTRY =
            new StorageDataHandlerRegistry(new JsonStorageHandler(new ObjectMapper()));

    private final IDao<MetadataMap> metadataDao;
    private final MetadataMap metadataCache;

    public AbstractDao(DaoFactory.IDataSourceFactory factory) {
        this.metadataDao = factory.create(MetadataMap.class, this.getClass().getSimpleName(), REGISTRY);
        MetadataMap loaded;
        try {
            loaded = metadataDao.load("");
        } catch (StorageException e) {
            // ignore;
            loaded = new MetadataMap();
        }
        metadataCache = loaded;
    }

    public synchronized Metadata putInCache(String locator, Metadata metadata) throws StorageException {
        if (metadata == null) {
            return null;
        }
        metadataCache.getMetadataMap().put(locator, metadata);
        metadataDao.saveOrUpdate("", metadataCache);
        return metadata;
    }

    protected Metadata loadMetadata(String locator, Function<String, Metadata> loader) {
        try {
            final var cached = metadataCache.getMetadataMap().get(locator);
            if (cached != null) {
                return cached;
            }
            return putInCache(locator, loader.apply(locator));
        } catch (StorageException e) {
            Logs.of(this).warning("Unable to save cache: " + e.getMessage());
            return null;
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
