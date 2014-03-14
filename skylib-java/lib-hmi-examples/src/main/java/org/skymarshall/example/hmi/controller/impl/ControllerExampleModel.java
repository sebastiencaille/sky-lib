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
package org.skymarshall.example.hmi.controller.impl;

import java.util.Comparator;

import org.skymarshall.example.hmi.TestObject;
import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.views.ListViews;
import org.skymarshall.hmi.mvc.HmiController;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;

public class ControllerExampleModel extends ControllerExampleObjectHmiModel {

    private static final class ComplexTestObjectComparator implements
            Comparator<TestObject> {
        @Override
        public int compare(final TestObject o1, final TestObject o2) {
            return o1.aFirstValue.compareTo(o2.aFirstValue);
        }
    }

    public ControllerExampleModel(final HmiController controller) {
        super(controller);
    }

    private final ObjectProperty<String>     listObjectProperty        = new ObjectProperty<>("ListObjectProperty",
                                                                               propertySupport);

    private final ObjectProperty<String>     dynamicListObjectProperty = new ObjectProperty<>(
                                                                               "DynamicListObjectProperty",
                                                                               propertySupport);

    private final ObjectProperty<TestObject> complexProperty           = new ObjectProperty<>("ComplexObject",
                                                                               propertySupport);

    final ListModel<TestObject>              tableModel                = new ListModel<>(
                                                                               ListViews
                                                                                       .sorted(new ComplexTestObjectComparator()));

    public ObjectProperty<String> getListObjectProperty() {
        return listObjectProperty;
    }

    public ObjectProperty<String> getDynamicListObjectProperty() {
        return dynamicListObjectProperty;
    }

    public ObjectProperty<TestObject> getComplexProperty() {
        return complexProperty;
    }

    public ListModel<TestObject> getTableModel() {
        return tableModel;
    }

}
