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
package ch.scaille.example.gui.controllermodel.impl;

import static ch.scaille.gui.mvc.GuiModel.of;

import ch.scaille.example.gui.TestObject;
import ch.scaille.example.gui.TestObjectGuiModel;
import ch.scaille.example.gui.controllermodel.impl.TestObjectControllerModelFrameModel.Columns;
import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.mvc.GuiController;
import ch.scaille.gui.swing.model.ObjectControllerTableModel;
import ch.scaille.javabeans.Converters;
import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.util.helpers.JavaExt;

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
		testObjectModel.getAFirstValueProperty().bind(this.createColumnBinding(Columns.VAL1));
		testObjectModel.getASecondValueProperty().bind(Converters.intToString())
				.bind(this.createColumnBinding(Columns.VAL2));
	}

	@Override
	public String getColumnName(final int column) {
		switch (Columns.values()[column]) {
		case VAL1:
			return "Value1";
		case VAL2:
			return "Value2";
		default:
			throw JavaExt.notImplemented();
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
			throw JavaExt.notImplemented();
		}
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return true;
	}

}
