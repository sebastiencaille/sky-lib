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
import org.skymarshall.hmi.mvc.properties.BooleanProperty;
import org.skymarshall.hmi.mvc.properties.ErrorNotifier;

public abstract class AbstractBooleanConverter<C> extends AbstractConverter<Boolean, C> {

    public AbstractBooleanConverter() {
    }

    private BooleanProperty booleanProperty;

    public IBindingController<C> bindWithProperty(final BooleanProperty aProperty, final ErrorNotifier errorNotifier) {
        this.booleanProperty = aProperty;
        return super.bind(aProperty, errorNotifier);
    }

    @Override
    public Boolean getPropertyValue() {
        return Boolean.valueOf(booleanProperty.getValue());
    }

    @Override
    public void setPropertyValue(final Object source, final Boolean propertyValue) {
        if (propertyValue == null) {
            throw new IllegalArgumentException("Null value is not accepted");
        }
        booleanProperty.setValue(source, propertyValue.booleanValue());
    }
}
