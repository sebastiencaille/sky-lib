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
import org.skymarshall.hmi.mvc.converters.AbstractFloatConverter;
import org.skymarshall.hmi.mvc.converters.FloatToFloatConverter;
import org.skymarshall.hmi.mvc.objectaccess.IObjectAccess;

/**
 * Property containing a float value.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 */
public class FloatProperty extends PrimitiveProperty {

    private float       value;
    private final float defaultValue;

    public FloatProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
            final ErrorProperty errorProperty, final IObjectAccess<?> access, final float defaultValue) {
        super(name, propertySupport, errorProperty, access);
        this.defaultValue = defaultValue;
    }

    public FloatProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
            final ErrorProperty errorProperty, final IObjectAccess<?> access) {
        this(name, propertySupport, errorProperty, access, 0.0f);
    }

    public void bind(final AbstractFloatConverter<?> converter) {
        converter.bindWithProperty(this, errorNotifier);
    }

    public IBindingController<Float> bind(final IComponentBinding<Float> binding) {
        final IBindingController<Float> controller = new FloatToFloatConverter().bindWithProperty(this, errorNotifier);
        controller.bind(binding);
        return controller;
    }

    public void setValue(final Object caller, final float newValue) {
        fireEvent(caller, EventKind.BEFORE);
        try {
            final float oldValue = value;
            value = newValue;
            propertySupport.firePropertyChange(getName(), caller, Float.valueOf(oldValue), Float.valueOf(newValue));
        } finally {
            fireEvent(caller, EventKind.AFTER);
        }
    }

    public float getValue() {
        return value;
    }

    @Override
    public void attach() {
        super.attach();
        propertySupport.firePropertyChange(getName(), this, null, Float.valueOf(value));
    }

    @Override
    public void reset(final Object caller) {
        setValue(this, defaultValue);
    }

    @Override
    protected void loadValueFromObject(final Object caller, final Object object, final IObjectAccess<?> access) {
        setValue(caller, access.getFloat(object));
    }

    @Override
    protected void saveValueIntoObject(final Object object, final IObjectAccess<?> access) {
        access.setFloat(object, value);
    }
}
