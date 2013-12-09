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
package org.skymarshall.example.hmi.model.impl;

import org.skymarshall.example.hmi.TestObject;
import org.skymarshall.hmi.model.views.IListView;
import org.skymarshall.hmi.model.views.ListView;
import org.skymarshall.hmi.mvc.converters.AbstractBooleanConverter;
import org.skymarshall.hmi.mvc.converters.ConversionException;

public class Converters {

    public static AbstractBooleanConverter<IListView<TestObject>> booleanToFilter() {

        return new AbstractBooleanConverter<IListView<TestObject>>() {

            @Override
            protected IListView<TestObject> convertPropertyValueToComponentValue(final Boolean propertyValue) {
                if (propertyValue.booleanValue()) {
                    return ListView.filtered(TableModelExampleView.FILTER);
                } else {
                    return ListView.<TestObject> inherited();
                }
            }

            @Override
            protected Boolean convertComponentValueToPropertyValue(final IListView<TestObject> componentValue)
                    throws ConversionException {
                return null;
            }

        };
    }

    public static AbstractBooleanConverter<IListView<TestObject>> booleanToOrder() {

        return new AbstractBooleanConverter<IListView<TestObject>>() {

            @Override
            protected IListView<TestObject> convertPropertyValueToComponentValue(final Boolean propertyValue) {
                if (propertyValue.booleanValue()) {
                    return ListView.sorted(TableModelExampleView.NORMAL_ORDER);
                } else {
                    return ListView.sorted(TableModelExampleView.REVERSE_ORDER);
                }
            }

            @Override
            protected Boolean convertComponentValueToPropertyValue(final IListView<TestObject> componentValue)
                    throws ConversionException {
                return null;
            }

        };
    }

}
