/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc.properties;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.IBindingController;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.PropertyEvent.EventKind;
import org.skymarshall.hmi.mvc.converters.AbstractObjectConverter;
import org.skymarshall.hmi.mvc.converters.IdentityObjectConverter;
import org.skymarshall.hmi.mvc.objectaccess.IObjectAccess;

public class ObjectProperty<T> extends AbstractProperty {

    private final IObjectAccess<T> objectAccess;

    private T                      value;

    private T                      valueWhenDetached;

    private T                      defaultValue;

    public ObjectProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
            final ErrorProperty errorProperty, final IObjectAccess<T> access, final T defaultValue) {
        super(name, propertySupport, errorProperty);
        this.objectAccess = access;
        this.defaultValue = defaultValue;
    }

    public ObjectProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
            final ErrorProperty errorProperty, final IObjectAccess<T> access) {
        this(name, propertySupport, errorProperty, access, null);
    }

    public <C> IBindingController<C> bind(final AbstractObjectConverter<T, C> converter) {
        return converter.bindWithProperty(this, errorNotifier);
    }

    public IBindingController<T> bind(final IComponentBinding<T> binding) {
        final IBindingController<T> controller = bind(new IdentityObjectConverter<T>());
        controller.bind(binding);
        return controller;
    }

    public void setValue(final Object caller, final T newValue) {
        fireEvent(caller, EventKind.BEFORE);
        try {
            final T oldValue = value;
            value = newValue;
            if (attached && (value != null || newValue != null)) {
                propertySupport.firePropertyChange(getName(), caller, oldValue, newValue);
            }
        } finally {
            fireEvent(caller, EventKind.AFTER);
        }
    }

    public void forceChanged(final Object caller) {
        propertySupport.firePropertyChange(getName(), caller, null, getValue());
    }

    protected boolean valueEquals(final T newValue) {
        return newValue == value || (newValue != null && value != null && newValue.equals(value));
    }

    public T getValue() {
        return value;
    }

    @Override
    public void detach() {
        valueWhenDetached = getValue();
        super.detach();
    }

    public T getValueWhenDetached() {
        return valueWhenDetached;
    }

    @Override
    public void attach() {
        attach(valueWhenDetached);
    }

    protected void attach(final T oldValue) {
        super.attach();
        if (oldValue != null || value != null) {
            propertySupport.firePropertyChange(getName(), this, oldValue, value);
        }
        valueWhenDetached = null;
    }

    @Override
    public void reset(final Object caller) {
        setValue(this, defaultValue);
    }

    @Override
    public void loadFrom(final Object caller, final Object object) {
        setValue(caller, objectAccess.getObject(object));
    }

    @Override
    public void saveInto(final Object object) {
        objectAccess.setObject(object, value);
    }

}
