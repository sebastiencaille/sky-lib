package ch.skymarshall.tcwriter.gui.editors.params;

import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.swing.model.ListModelTableModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;

public class TestParameterValueTableModel
		extends ListModelTableModel<TestParameterValueTableModel.ParameterValue, TestParameterValueTableModel.Columns> {

	public enum Columns {
		MANDATORY, ENABLED, DESCRIPTION, VALUE
	}

	static class ParameterValue implements Comparable<ParameterValue> {
		final String id;
		final TestParameterFactory factory;
		final String description;
		boolean mandatory;
		boolean enabled;
		String value;
		boolean visible;

		ParameterValue(final String id, final TestParameterFactory parameterFactory, final String description,
				final String value, final boolean enabled) {
			this.id = id;
			this.factory = parameterFactory;
			this.enabled = enabled;
			this.description = description;
			this.value = value;
		}

		public void update(final boolean mandatory, final boolean visible) {
			this.mandatory = mandatory;
			this.visible = visible;
		}

		@Override
		public int compareTo(final ParameterValue other) {
			if (mandatory && !other.mandatory) {
				return -1;
			} else if (!mandatory && other.mandatory) {
				return 1;
			}
			return description.compareTo(other.description);
		}
	}

	public TestParameterValueTableModel(final ListModel<ParameterValue> paramList) {
		super(paramList, Columns.class);
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return columnIndex == getIndexOf(Columns.ENABLED)
				|| (columnIndex == getIndexOf(Columns.VALUE) && getObjectAtRow(rowIndex).factory.hasType());
	}

	@Override
	protected Object getValueAtColumn(final ParameterValue object, final Columns column) {
		switch (column) {
		case MANDATORY:
			return object.mandatory;
		case ENABLED:
			return object.enabled || object.mandatory;
		case DESCRIPTION:
			return object.description;
		case VALUE:
			if (!object.factory.hasType()) {
				return "<no value>";
			}
			return object.value;
		default:
			throw new IllegalStateException("Unknown column " + column);
		}
	}

	@Override
	protected void setValueAtColumn(final ParameterValue object, final Columns column, final Object value) {
		switch (column) {
		case ENABLED:
			object.enabled = (Boolean) value;
			break;
		case VALUE:
			object.value = (String) value;
			break;
		default:
			throw new IllegalStateException("Not editable" + column);
		}
	}

}
