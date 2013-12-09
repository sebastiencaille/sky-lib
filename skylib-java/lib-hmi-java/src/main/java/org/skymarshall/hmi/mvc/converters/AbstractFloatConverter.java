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
package org.skymarshall.hmi.mvc.converters;

import org.skymarshall.hmi.mvc.IBindingController;
import org.skymarshall.hmi.mvc.properties.ErrorNotifier;
import org.skymarshall.hmi.mvc.properties.FloatProperty;

public abstract class AbstractFloatConverter<C> extends AbstractConverter<Float, C> {

    public AbstractFloatConverter() {
    }

    private FloatProperty floatProperty;

    public IBindingController<C> bindWithProperty(final FloatProperty aProperty, final ErrorNotifier errorNotifier) {
        this.floatProperty = aProperty;
        return super.bind(aProperty, errorNotifier);
    }

    @Override
    public Float getPropertyValue() {
        return Float.valueOf(floatProperty.getValue());
    }

    @Override
    public void setPropertyValue(final Object source, final Float propertyValue) {
        if (propertyValue == null) {
            throw new IllegalArgumentException("Null value is not accepted");
        }
        floatProperty.setValue(source, propertyValue.floatValue());
    }
}
