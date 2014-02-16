package org.skymarshall.hmi.mvc.properties;

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

    public U property() {
        return property;
    }

    public static <T, U extends AbstractTypedProperty<T>> Properties<T, U> of(final U property) {
        return new Properties<T, U>(property);
    }

    public static <T, U extends AbstractTypedProperty<T>> U persistent(final U property, final IObjectAccess<T> access) {
        property.setObjectAccess(access);
        return property;
    }

}
