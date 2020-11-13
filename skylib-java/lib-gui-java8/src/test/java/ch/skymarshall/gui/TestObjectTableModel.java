package ch.skymarshall.gui;

import ch.skymarshall.gui.TestObjectTableModel.Columns;
import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.swing.model.ListModelTableModel;

public  class TestObjectTableModel extends ListModelTableModel<TestObject, Columns> {

	public enum Columns {
		VAL;
	}

	
	public TestObjectTableModel(final ListModel<TestObject> model) {
		super(model, Columns.class);
	}

	@Override
	protected Object getValueAtColumn(final TestObject object, final Columns column) {
		return object.val;
	}

	@Override
	protected void setValueAtColumn(final TestObject object, final Columns column, final Object value) {
		// no op
	}
}