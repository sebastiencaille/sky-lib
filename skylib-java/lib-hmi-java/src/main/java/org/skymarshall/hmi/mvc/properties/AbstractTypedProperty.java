package org.skymarshall.hmi.mvc.properties;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;

public abstract class AbstractTypedProperty<T> extends AbstractProperty {

    public AbstractTypedProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
            final ErrorNotifier errorNotifier) {
        super(name, propertySupport, errorNotifier);
    }

    public abstract T getObjectValue();

    public abstract void setObjectValue(final Object caller, final T newValue);

}
