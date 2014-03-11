package org.skymarshall.hmi.mvc.properties;

public interface IPersister<T> {
    T get();

    void set(T value);

}
