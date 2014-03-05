package org.skymarshall.hmi.mvc.properties;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.objectaccess.IObjectAccess;

public abstract class AbstractTypedProperty<T> extends AbstractProperty {

    private IObjectAccess<T> objectAccess;

    public AbstractTypedProperty(final String name, final ControllerPropertyChangeSupport propertySupport) {
        super(name, propertySupport);
    }

    public void setObjectAccess(final IObjectAccess<T> objectAccess) {
        this.objectAccess = objectAccess;
    }

    @Override
    public void loadFrom(final Object caller, final Object object) {
        setObjectValue(caller, objectAccess.get(object));
    }

    @Override
    public void saveInto(final Object object) {
        objectAccess.set(object, getObjectValue());
    }

    public void setObjectValueFromComponent(final Object caller, final T newValue) {
        if (attached) {
            setObjectValue(caller, newValue);
        }
    }

    public abstract T getObjectValue();

    public abstract void setObjectValue(final Object caller, final T newValue);

}
