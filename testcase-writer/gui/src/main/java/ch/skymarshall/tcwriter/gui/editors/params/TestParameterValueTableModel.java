package ch.skymarshall.tcwriter.gui.editors.params;

import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.swing.model.ListModelTableModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition;

public class TestParameterValueTableModel
		extends ListModelTableModel<TestParameterValueTableModel.ParameterValue, TestParameterValueTableModel.Columns> {

	public enum Columns {
		ENABLED, DESCRIPTION, VALUE
	}

	static class ParameterValue implements Comparable<ParameterValue> {
		final String id;
		final TestParameterDefinition parameterDefinition;
		final String description;
		final boolean mandatory;
		boolean enabled;
		String value;

		ParameterValue(final String id, final TestParameterDefinition parameterDefinition, final boolean enabled,
				final String description, final String value, final boolean mandatory) {
			this.id = id;
			this.parameterDefinition = parameterDefinition;
			this.enabled = enabled || mandatory;
			this.description = description;
			this.value = value;
			this.mandatory = mandatory;

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
	protected Object getValueAtColumn(final ParameterValue object, final Columns column) {
		switch (column) {
		case ENABLED:
			return object.enabled || object.mandatory;
		case DESCRIPTION:
			return object.description;
		case VALUE:
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
