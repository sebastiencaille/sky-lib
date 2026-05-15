package ch.scaille.util.persistence;

import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

public class DummyDao<T> implements IDao<T> {
    @Override
    public Stream<ResourceMetaData> list() {
        return Stream.empty();
    }

    @Override
    public ResourceMetaData resolve(String locator) {
        throw new IllegalStateException("Dao not initialized");
    }

    @Override
    public ResourceMetaData resolve(String locator, String mimetype) {
        throw new IllegalStateException("Dao not initialized");
    }

    @Override
    public ResourceMetaData resolveOrCreate(String locator) {
        throw new IllegalStateException("Dao not initialized");
    }

    @Override
    public Resource<T> loadResource(ResourceMetaData metadata, @Nullable T template) {
        throw new IllegalStateException("Dao not initialized");
    }

    @Override
    public Resource<T> loadResource(String locator, @Nullable T template) {
        throw new IllegalStateException("Dao not initialized");
    }

    @Override
    public Resource<T> saveOrUpdate(String locator, T value) {
        throw new IllegalStateException("Dao not initialized");
    }

    @Override
    public Resource<T> saveOrUpdate(Resource<T> value) {
        return null;
    }
}
