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

import org.skymarshall.hmi.mvc.AbstractLink;
import org.skymarshall.hmi.mvc.HmiErrors;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

public abstract class AbstractConverter<PropertyType, ComponentType> extends AbstractLink<PropertyType, ComponentType> {

    protected abstract ComponentType convertPropertyValueToComponentValue(final PropertyType propertyValue);

    protected abstract PropertyType convertComponentValueToPropertyValue(ComponentType componentValue)
            throws ConversionException;

    public AbstractConverter() {
    }

    @Override
    protected void setValueFromProperty(final AbstractProperty source, final PropertyType value) {
        final ComponentType converted = convertPropertyValueToComponentValue(value);
        bindingTo.setComponentSideValue(source, converted);
    }

    /**
     * Called by binding to set the value provided by the component
     * 
     * @param source
     * @param componentValue
     */
    @Override
    public void setValueFromComponent(final Object source, final ComponentType componentValue) {
        try {
            bindingFrom.setPropertySideValue(source, convertComponentValueToPropertyValue(componentValue));
        } catch (final ConversionException e) {
            getErrorNotifier().setError(bindingTo.getComponent(), HmiErrors.fromException(e));
        }
    }

}
