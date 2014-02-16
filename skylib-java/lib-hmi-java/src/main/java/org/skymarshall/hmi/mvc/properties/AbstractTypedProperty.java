package org.skymarshall.hmi.mvc.properties;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.PropertyEvent.EventKind;
import org.skymarshall.hmi.mvc.objectaccess.IObjectAccess;

public abstract class AbstractTypedProperty<T> extends AbstractProperty {

    private IObjectAccess<T> objectAccess;
    private Object           autoCommitObject;

    public AbstractTypedProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
            final ErrorNotifier errorNotifier) {
        super(name, propertySupport, errorNotifier);
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

    public void setAutoCommitObject(final Object autoCommitObject) {
        this.autoCommitObject = autoCommitObject;
    }

    protected void autoCommit() {
        if (autoCommitObject != null) {
            saveInto(autoCommitObject);
        }
    }

    @Override
    protected void onValueSet(final Object caller, final EventKind eventKind) {
        autoCommit();
        super.onValueSet(caller, eventKind);
    }

    public abstract T getObjectValue();

    public abstract void setObjectValue(final Object caller, final T newValue);

}