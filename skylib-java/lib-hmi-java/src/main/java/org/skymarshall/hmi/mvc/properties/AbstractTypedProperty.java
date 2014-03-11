package org.skymarshall.hmi.mvc.properties;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;

/**
 * A property with a typed value.
 * <p>
 * 
 * @author Sebastien Caille
 *
 * @param <T>
 *            the type of the object contained int the property
 */
public abstract class AbstractTypedProperty<T> extends AbstractProperty {

    private IPersister<T> persister;

    public AbstractTypedProperty(final String name, final ControllerPropertyChangeSupport propertySupport) {
        super(name, propertySupport);
    }

    public void setPersister(final IPersister<T> persister) {
        this.persister = persister;
    }

    @Override
    public void load(final Object caller) {
        if (persister != null) {
            setObjectValue(caller, persister.get());
        }
    }

    @Override
    public void save() {
        persister.set(getObjectValue());
    }

    public void setObjectValueFromComponent(final Object caller, final T newValue) {
        if (attached) {
            setObjectValue(caller, newValue);
        }
    }

    public abstract T getObjectValue();

    public abstract void setObjectValue(final Object caller, final T newValue);

}
