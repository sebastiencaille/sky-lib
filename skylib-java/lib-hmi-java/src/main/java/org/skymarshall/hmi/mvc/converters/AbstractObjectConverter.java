/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc.converters;

import org.skymarshall.hmi.mvc.IBindingController;
import org.skymarshall.hmi.mvc.properties.AbstractTypedProperty;
import org.skymarshall.hmi.mvc.properties.ErrorNotifier;

/**
 * Root of all type converters.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 * @param <T>
 *            the property type
 * @param <C>
 *            the component type
 */
public abstract class AbstractObjectConverter<T, C> extends AbstractConverter<T, C> {

    private AbstractTypedProperty<T> property;

    public AbstractObjectConverter() {
    }

    public IBindingController<C> bindWithProperty(final AbstractTypedProperty<T> aProperty,
            final ErrorNotifier theErrorProperty) {
        this.property = aProperty;
        return super.bind(aProperty, theErrorProperty);
    }

    @Override
    public T getPropertyValue() {
        return property.getObjectValue();
    }

}
