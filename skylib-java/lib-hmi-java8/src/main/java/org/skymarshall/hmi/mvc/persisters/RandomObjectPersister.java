package org.skymarshall.hmi.mvc.persisters;

import org.skymarshall.hmi.mvc.properties.IPersister;

public class RandomObjectPersister<T> implements
        IPersister<T> {

    private final FieldAccess<T> fieldAccess;
    private Object               target;

    public RandomObjectPersister(final FieldAccess<T> fieldAccess) {
        this.fieldAccess = fieldAccess;
    }

    public void setTarget(final Object target) {
        this.target = target;
    }

    @Override
    public T get() {
        if (target == null) {
            throw new IllegalStateException("No target object defined");
        }
        return fieldAccess.get(target);
    }

    @Override
    public void set(final T value) {
        if (target == null) {
            throw new IllegalStateException("No target object defined");
        }
        fieldAccess.set(target, value);
    }

}
