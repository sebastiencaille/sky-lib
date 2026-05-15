package ch.scaille.tcwriter.server.dao;

import ch.scaille.tcwriter.persistence.IModelDao;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class AbstractDao {

    protected final IModelDao modelDao;

    public AbstractDao(IModelDao modelDao) {
        this.modelDao = modelDao;
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
