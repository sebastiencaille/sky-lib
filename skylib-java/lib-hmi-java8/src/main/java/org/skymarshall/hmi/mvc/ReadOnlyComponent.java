package org.skymarshall.hmi.mvc;


public abstract class ReadOnlyComponent<T> implements
        IComponentBinding<T> {

    @Override
    public Object getComponent() {
        return null;
    }

    @Override
    public void addComponentValueChangeListener(final IComponentLink<T> link) {
        // Read only
    }

}
