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
import org.skymarshall.hmi.mvc.converters.AbstractBooleanConverter;
import org.skymarshall.hmi.mvc.converters.AbstractObjectConverter;
import org.skymarshall.hmi.mvc.converters.BooleanToBooleanConverter;

/**
 * Property containing a boolean value.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 */
public class BooleanProperty extends AbstractTypedProperty<Boolean> {
    private boolean       value;
    private final boolean defaultValue;

    public BooleanProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
            final ErrorProperty errorProperty, final boolean defaultValue) {
        super(name, propertySupport, errorProperty);
        this.defaultValue = defaultValue;
    }

    public BooleanProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
            final ErrorProperty errorProperty) {
        this(name, propertySupport, errorProperty, false);
    }

    public <C> IBindingController<C> bind(final AbstractBooleanConverter<C> converter) {
        return converter.bindWithProperty(this, errorNotifier);
    }

    public <C> IBindingController<C> bind(final AbstractObjectConverter<Boolean, C> converter) {
        return converter.bindWithProperty(this, errorNotifier);
    }

    public IBindingController<Boolean> bind(final IComponentBinding<Boolean> binding) {
        final IBindingController<Boolean> controller = new BooleanToBooleanConverter().bindWithProperty(this,
                errorNotifier);
        controller.bind(binding);
        return controller;
    }

    public void setValue(final Object caller, final boolean newValue) {
        onValueSet(caller, EventKind.BEFORE);
        try {
            final boolean oldValue = value;
            value = newValue;
            propertySupport.firePropertyChange(getName(), caller, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
        } finally {
            onValueSet(caller, EventKind.AFTER);
        }
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void setObjectValue(final Object caller, final Boolean newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException("Null value is not allowed");
        }
        setValue(caller, newValue.booleanValue());
    }

    @Override
    public Boolean getObjectValue() {
        return Boolean.valueOf(getValue());
    }

    @Override
    public void reset(final Object caller) {
        setValue(this, defaultValue);
    }

    @Override
    public void attach() {
        super.attach();
        propertySupport.firePropertyChange(getName(), this, null, Boolean.valueOf(value));
    }

}
