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
package org.skymarshall.example.hmi.controllermodel.impl;

import org.skymarshall.example.hmi.TestObject;
import org.skymarshall.example.hmi.TestObjectHmiModel;
import org.skymarshall.example.hmi.controllermodel.impl.TestObjectControllerModelFrameModel.Columns;
import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.mvc.HmiController;
import org.skymarshall.hmi.mvc.converters.Converters;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.swing17.model.ObjectControllerTableModel;

@SuppressWarnings("serial")
public class TestObjectControllerModelFrameModel extends
        ObjectControllerTableModel<TestObject, TestObjectHmiModel, Columns> {

    public static enum Columns {
        VAL1, VAL2
    }

    public TestObjectControllerModelFrameModel(final HmiController controller, final ListModel<TestObject> model) {
        super(model, new TestObjectHmiModel(controller), Columns.class);
    }

    @Override
    protected void bindController(final TestObjectHmiModel testObjectModel) {
        testObjectModel.getAFirstValueProperty().bind(this.<String> getColumnBinding(Columns.VAL1));
        testObjectModel.getASecondValueProperty().bind(Converters.intToString())
                .bind(this.<String> getColumnBinding(Columns.VAL2));
    }

    @Override
    public String getColumnName(final int column) {
        switch (Columns.values()[column]) {
        case VAL1:
            return "Value1";
        case VAL2:
            return "Value2";
        default:
            return null;
        }
    }

    @Override
    protected AbstractProperty getPropertyAt(final TestObjectHmiModel controller, final Columns column) {
        switch (column) {
        case VAL1:
            return controller.getAFirstValueProperty();
        case VAL2:
            return controller.getASecondValueProperty();
        default:
            return null;
        }
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return true;
    }

}
