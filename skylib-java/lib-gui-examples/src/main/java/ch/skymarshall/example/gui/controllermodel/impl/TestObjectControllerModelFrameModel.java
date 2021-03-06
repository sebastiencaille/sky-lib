/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.example.gui.controllermodel.impl;

import static ch.skymarshall.gui.mvc.GuiModel.of;

import ch.skymarshall.example.gui.TestObject;
import ch.skymarshall.example.gui.TestObjectGuiModel;
import ch.skymarshall.example.gui.controllermodel.impl.TestObjectControllerModelFrameModel.Columns;
import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.mvc.GuiController;
import ch.skymarshall.gui.mvc.factories.Converters;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.gui.swing.model.ObjectControllerTableModel;

@SuppressWarnings("serial")
public class TestObjectControllerModelFrameModel
		extends ObjectControllerTableModel<TestObject, TestObjectGuiModel, Columns> {

	public enum Columns {
		VAL1, VAL2
	}

	public TestObjectControllerModelFrameModel(final GuiController controller, final ListModel<TestObject> model) {
		super(model, new TestObjectGuiModel(of(controller)), Columns.class);
	}

	@Override
	protected void bindModel(final TestObjectGuiModel testObjectModel) {
		testObjectModel.getAFirstValueProperty().bind(this.<String>createColumnBinding(Columns.VAL1));
		testObjectModel.getASecondValueProperty().bind(Converters.intToString())
				.bind(this.<String>createColumnBinding(Columns.VAL2));
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
	protected AbstractProperty getPropertyAt(final TestObjectGuiModel controller, final Columns column) {

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
