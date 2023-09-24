package ch.scaille.gui;

import ch.scaille.gui.TestObjectTableModel.Columns;
import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.swing.model.ListModelTableModel;

public class TestObjectTableModel extends ListModelTableModel<TestObject, Columns> {

	public enum Columns {
		VAL
	}

	public TestObjectTableModel(final ListModel<TestObject> model) {
		super(model, Columns.class);
	}

	@Override
	protected Object getValueAtColumn(final TestObject object, final Columns column) {
		return object.getVal();
	}

	@Override
	protected void setValueAtColumn(final TestObject object, final Columns column, final Object value) {
		// no op
	}
}