package ch.scaille.tcwriter.persistence;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.persistence.handlers.JsonStorageHandler;
import ch.scaille.util.persistence.DaoFactory;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.StorageException;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import lombok.Getter;
import lombok.extern.java.Log;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Log
public class MetadataCacheDao {

    @Getter
    public static class MetadataCache {
        private final Map<String, Metadata> metadataMap = new HashMap<>();
    }

    private static final StorageDataHandlerRegistry REGISTRY =
            new StorageDataHandlerRegistry(new JsonStorageHandler(new ObjectMapper()));

    private final IDao<MetadataCache> metadataDao;
    private final MetadataCache metadataCache;

    public MetadataCacheDao(String cacheName, DaoFactory.IDataSourceFactory cacheDsFactory) {
        this.metadataDao = cacheDsFactory.create(MetadataCache.class, "Cache/Cache-" + cacheName, REGISTRY);
        MetadataCache loaded;
        try {
            loaded = metadataDao.load("cache.json");
        } catch (StorageException _) {
            // ignore
            loaded = new MetadataCache();
        }
        metadataCache = loaded;
    }

    @Nullable
    public synchronized Metadata putInCache(String identifier, @Nullable Metadata metadata) throws StorageException {
        if (metadata == null) {
            return null;
        }
        metadataCache.getMetadataMap().put(identifier, metadata);
        metadataDao.saveOrUpdate("cache.json", metadataCache);
        return metadata;
    }

    @Nullable
    protected Metadata loadMetadata(String identifier, Function<String, @Nullable Metadata> loader) {
        try {
            final var cached = metadataCache.getMetadataMap().get(identifier);
            if (cached != null) {
                return cached;
            }
            return putInCache(identifier, loader.apply(identifier));
        } catch (StorageException e) {
            log.warning("Unable to save cache: " + e.getMessage());
            return null;
        }
    }

}
