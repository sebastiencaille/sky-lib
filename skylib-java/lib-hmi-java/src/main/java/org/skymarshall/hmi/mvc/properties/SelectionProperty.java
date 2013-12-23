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

import java.beans.PropertyChangeListener;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.IBindingController;
import org.skymarshall.hmi.mvc.converters.AbstractObjectConverter;
import org.skymarshall.hmi.mvc.objectaccess.IObjectAccess;

public class SelectionProperty<T> extends ObjectProperty<T> {

    private final SelectionFixProperty<T> selectionFixProperty;

    public SelectionProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
            final ErrorProperty errorProperty, final IObjectAccess<T> access) {
        super(name, propertySupport, errorProperty, access);
        selectionFixProperty = new SelectionFixProperty<T>(name, propertySupport, errorProperty);
    }

    public void addListSelectionFixListener(final PropertyChangeListener propertyChangeListener) {
        selectionFixProperty.addListener(propertyChangeListener);
    }

    @Override
    public <C> IBindingController<C> bind(final AbstractObjectConverter<T, C> anObjectConverter) {
        anObjectConverter.listenToProperty(selectionFixProperty);
        return super.bind(anObjectConverter);
    }

    public void fix() {
        setValue(null, getValueAtDetach());
        selectionFixProperty.fire(getValueAtDetach());
    }

}
