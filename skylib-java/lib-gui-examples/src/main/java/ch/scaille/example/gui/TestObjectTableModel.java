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
package ch.scaille.example.gui;

import ch.scaille.example.gui.TestObjectTableModel.Columns;
import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.swing.model.ListModelTableModel;

public class TestObjectTableModel extends ListModelTableModel<TestObject, Columns> {

	private static final long serialVersionUID = 5035556443525716721L;

	public enum Columns {
		A_FIRST_VALUE, A_SECOND_VALUE
	}

	public TestObjectTableModel(final ListModel<TestObject> source) {
		super(source, Columns.class);
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return Columns.A_FIRST_VALUE.ordinal() == columnIndex;
	}

	@Override
	protected Object getValueAtColumn(final TestObject object, final Columns column) {
		switch (column) {
		case A_FIRST_VALUE:
			return object.getAFirstValue();
		case A_SECOND_VALUE:
			return Integer.valueOf(object.getASecondValue());
		default:
			throw new IllegalArgumentException("Unknown column " + column);
		}
	}

	@Override
	protected void setValueAtColumn(final TestObject object, final Columns column, final Object value) {
		switch (column) {
		case A_FIRST_VALUE:
			object.setAFirstValue((String) value);
			break;
		case A_SECOND_VALUE:
			break;
		default:
			throw new IllegalArgumentException("Unknown column " + column);
		}
	}

}
