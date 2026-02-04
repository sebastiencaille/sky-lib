package ch.scaille.tcwriter.persistence;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.persistence.handlers.JsonStorageHandler;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.persistence.DaoFactory;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.StorageException;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import lombok.Getter;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MetadataCacheDao {

    @Getter
    private static class MetadataCache {
        private final Map<String, Metadata> metadataMap = new HashMap<>();
    }

    private static final StorageDataHandlerRegistry REGISTRY =
            new StorageDataHandlerRegistry(new JsonStorageHandler(new ObjectMapper()));

    private final IDao<MetadataCache> metadataDao;
    private final MetadataCache metadataCache;

    public MetadataCacheDao(String cacheName, DaoFactory.IDataSourceFactory cacheDsFactory) {
        this.metadataDao = cacheDsFactory.create(MetadataCache.class, cacheName, REGISTRY);
        MetadataCache loaded;
        try {
            loaded = metadataDao.load("");
        } catch (StorageException _) {
            // ignore
            loaded = new MetadataCache();
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

}
