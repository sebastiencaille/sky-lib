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
import org.skymarshall.hmi.model.views.ListView;
import org.skymarshall.hmi.mvc.HmiController;
import org.skymarshall.hmi.mvc.properties.SelectionProperty;

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

    private final SelectionProperty<String>     listSelectionProperty        = new SelectionProperty<>(
                                                                                     "ListSelectionProperty",
                                                                                     propertySupport);

    private final SelectionProperty<String>     dynamicListSelectionProperty = new SelectionProperty<>(
                                                                                     "DynamicListSelectionProperty",
                                                                                     propertySupport);

    private final SelectionProperty<TestObject> complexProperty              = new SelectionProperty<>("ComplexObject",
                                                                                     propertySupport);

    final ListModel<TestObject>                 tableModel                   = new ListModel<>(
                                                                                     ListView.sorted(new ComplexTestObjectComparator()));

    public SelectionProperty<String> getListSelectionProperty() {
        return listSelectionProperty;
    }

    public SelectionProperty<String> getDynamicListSelectionProperty() {
        return dynamicListSelectionProperty;
    }

    public SelectionProperty<TestObject> getComplexProperty() {
        return complexProperty;
    }

    public ListModel<TestObject> getTableModel() {
        return tableModel;
    }

}
