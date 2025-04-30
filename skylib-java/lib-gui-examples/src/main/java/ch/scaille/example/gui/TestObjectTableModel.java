package ch.scaille.example.gui;

import ch.scaille.example.gui.TestObjectTableModel.Columns;
import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.swing.model.ListModelTableModel;

import java.io.Serial;

public class TestObjectTableModel extends ListModelTableModel<TestObject, Columns> {

	@Serial
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
		return switch (column) {
			case A_FIRST_VALUE -> object.getAFirstValue();
			case A_SECOND_VALUE -> object.getASecondValue();
			default -> throw new IllegalArgumentException("Unknown column " + column);
		};
	}

	@Override
	protected void setValueAtColumn(final TestObject object, final Columns column, final Object value) {
		switch (column) {
			case A_FIRST_VALUE -> object.setAFirstValue((String) value);
			case A_SECOND_VALUE -> { /* nope */ }
			default -> throw new IllegalArgumentException("Unknown column " + column);
		}
	}

}
