package org.skymarshall.hmi.mvc.properties;

import org.skymarshall.hmi.mvc.AutoCommitListener;
import org.skymarshall.hmi.mvc.objectaccess.IObjectAccess;

/**
 * To tune the properties.
 * <p>
 * Can be used through static calls, or through instance (such as
 * Properties.of(property).persistent(...)...
 * 
 * @author Sebastien Caille
 *
 * @param <T>
 * @param <U>
 */
public class Properties<T, U extends AbstractTypedProperty<T>> {

    private final U property;

    private Properties(final U property) {
        this.property = property;
    }

    public Properties<T, U> persistent(final IObjectAccess<T> access) {
        persistent(property, access);
        return this;
    }

    public Properties<T, U> setErrorNotifier(final ErrorNotifier notifier) {
        setErrorNotifier(property, notifier);
        return this;
    }

    public Properties<T, U> autoCommitInto(final Object object) {
        autoCommitInto(property, object);
        return this;
    }

    public U getProperty() {
        return property;
    }

    public static <T, U extends AbstractTypedProperty<T>> Properties<T, U> of(final U property) {
        return new Properties<T, U>(property);
    }

    public static <T, U extends AbstractTypedProperty<T>> U persistent(final U property, final IObjectAccess<T> access) {
        property.setObjectAccess(access);
        return property;
    }

    public static <T, U extends AbstractTypedProperty<T>> U setErrorNotifier(final U property,
            final ErrorNotifier notifier) {
        property.setErrorNotifier(notifier);
        return property;
    }

    public static <T, U extends AbstractTypedProperty<T>> U autoCommitInto(final U property, final Object object) {
        property.addListener(new AutoCommitListener(object));
        return property;
    }
}
