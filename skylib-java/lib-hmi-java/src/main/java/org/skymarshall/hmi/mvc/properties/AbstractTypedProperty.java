/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
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
