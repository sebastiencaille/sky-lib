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

public class IntToStringConverter extends AbstractIntConverter<String> {

    public IntToStringConverter() {
    }

    @Override
    protected Integer convertComponentValueToPropertyValue(final String componentObject) throws ConversionException {
        if (componentObject == null) {
            throw new ConversionException("Null value is not allowed");
        }
        try {
            return Integer.valueOf(componentObject);
        } catch (final NumberFormatException e) {
            throw new ConversionException("Cannot convert to number", e);
        }
    }

    @Override
    protected String convertPropertyValueToComponentValue(final Integer value) {
        return String.valueOf(value);
    }

}
