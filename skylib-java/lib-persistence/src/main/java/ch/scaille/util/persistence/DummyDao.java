package ch.scaille.util.persistence;

import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

public class DummyDao<T> implements IDao<T> {
    @Override
    public Stream<ResourceMetaData> list() {
        return Stream.empty();
    }

    @Override
    public ResourceMetaData resolve(String identifier) {
        throw new IllegalStateException("Dao not initialized");
    }

    @Override
    public ResourceMetaData resolve(String identifier, String mimetype) {
        throw new IllegalStateException("Dao not initialized");
    }

    @Override
    public ResourceMetaData resolveOrCreate(String identifier) {
        throw new IllegalStateException("Dao not initialized");
    }

    @Override
    public Resource<T> loadResource(ResourceMetaData metadata, @Nullable T template) {
        throw new IllegalStateException("Dao not initialized");
    }

    @Override
    public Resource<T> loadResource(String identifier, @Nullable T template) {
        throw new IllegalStateException("Dao not initialized");
    }

    @Override
    public Resource<T> saveOrUpdate(String identifier, T value) {
        throw new IllegalStateException("Dao not initialized");
    }

    @Override
    public Resource<T> saveOrUpdate(Resource<T> value) {
        return null;
    }
}
