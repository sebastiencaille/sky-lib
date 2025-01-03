package ch.scaille.tcwriter.gui.editors.params;

import java.util.Objects;

import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.swing.model.ListModelTableModel;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import org.jetbrains.annotations.NotNull;

public class TestParameterValueTableModel extends
		ListModelTableModel<TestParameterValueTableModel.ParameterValueEntry, TestParameterValueTableModel.Columns> {

	public enum Columns {
		MANDATORY, ENABLED, DESCRIPTION, VALUE
	}

	/**
	 * Parameter edition
	 *
	 * @author scaille
	 *
	 */
	public static class ParameterValueEntry implements Comparable<ParameterValueEntry> {
		final String id;
		final TestParameterFactory factory;
		final String description;
		boolean mandatory;
		boolean enabled;
		String value;
		boolean visible;

		ParameterValueEntry(final String id, final TestParameterFactory parameterFactory, final String description,
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
		public int compareTo(final @NotNull ParameterValueEntry other) {
			if (mandatory && !other.mandatory) {
				return -1;
			} else if (!mandatory && other.mandatory) {
				return 1;
			}
			return description.compareTo(other.description);
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof final ParameterValueEntry other && mandatory == other.mandatory
					&& Objects.equals(description, other.description);
		}

		@Override
		public int hashCode() {
			return description.hashCode() + (mandatory ? 1 : 3);
		}

		@Override
		public String toString() {
			return id + ":[" + enabled + ", " + value;
		}
	}

	public TestParameterValueTableModel(final ListModel<ParameterValueEntry> paramList) {
		super(paramList, Columns.class);
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return columnIndex == getIndexOf(Columns.ENABLED)
				|| (columnIndex == getIndexOf(Columns.VALUE) && getObjectAtRow(rowIndex).factory.hasType());
	}

	@Override
	protected Object getValueAtColumn(final ParameterValueEntry object, final Columns column) {
		return switch (column) {
			case MANDATORY -> object.mandatory;
			case ENABLED -> object.enabled || object.mandatory;
			case DESCRIPTION -> object.description;
			case VALUE -> object.factory.hasType() ? object.value : "<no value>";
			default -> throw new IllegalStateException("Unknown column " + column);
		};
	}

	@Override
	protected void setValueAtColumn(final ParameterValueEntry object, final Columns column, final Object value) {
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
