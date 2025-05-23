package ch.scaille.gui.swing.model;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import ch.scaille.gui.model.ListModel;
import ch.scaille.util.helpers.Logs;

import java.io.Serial;

/**
 * Table model based on a {@link ListModel}, with enum based column identifiers.
 * <p>
 * The order of the columns is based on the ordinal of the enum components.
 *
 * @author Sebastien Caille
 *
 * @param <T> type of data contained in the model
 * @param <C> enum of columns describing the model
 */
public abstract class ListModelTableModel<T, C extends Enum<C>> extends AbstractTableModel implements ListDataListener {

	@Serial
	private static final long serialVersionUID = -877625721248108739L;

	public static final int TABLE_ABOUT_TO_CHANGE = 98;
	public static final int TABLE_CHANGE_DONE = 99;

	protected final ListModel<T> model;
	private final Class<C> columnsEnumClass;
	private final Enum<C>[] columnsEnum;

	protected int warningLimit = 20;

	protected abstract Object getValueAtColumn(T object, C column);

	protected abstract void setValueAtColumn(T object, C column, Object value);

	protected ListModelTableModel(final ListModel<T> model, final Class<C> columnsEnumClass) {
		this.model = model;
		this.columnsEnumClass = columnsEnumClass;
		this.columnsEnum = columnsEnumClass.getEnumConstants();
		model.addListDataListener(this);
	}

	public ListModel<T> getBaseModel() {
		return model;
	}

	protected void setWarningLimit(final int warningLimit) {
		this.warningLimit = warningLimit;
	}

	protected void warn(final String string) {
		Logs.of(this).warning("WARNING: " + string); // NOSONAR
	}

	@Override
	public int getColumnCount() {
		return columnsEnum.length;
	}

	@Override
	public int getRowCount() {
		return model.getSize();
	}

	public int getIndexOf(final C column) {
		return column.ordinal();
	}

	@Override
	public Object getValueAt(final int row, final int column) {
		return getValueAtColumn(model.getValueAt(row), columnOf(column));
	}

	@Override
	public void setValueAt(final Object aValue, final int row, final int column) {
		super.setValueAt(aValue, row, column);
		final var editedValue = model.getValueAt(row);
		model.editValue(editedValue, v -> setValueAtColumn(model.getValueAt(row), columnOf(column), aValue));
	}

	@Override
	public void contentsChanged(final ListDataEvent event) {
		fireTableRowsUpdated(event.getIndex0(), event.getIndex1());
	}

	@Override
	public void intervalAdded(final ListDataEvent event) {
		fireTableChanged(new TableModelEvent(this, event.getIndex0(), event.getIndex1(), TableModelEvent.ALL_COLUMNS,
				TABLE_ABOUT_TO_CHANGE));
		fireTableRowsInserted(event.getIndex0(), event.getIndex1());
		fireTableChanged(new TableModelEvent(this, event.getIndex0(), event.getIndex1(), TableModelEvent.ALL_COLUMNS,
				TABLE_CHANGE_DONE));
	}

	@Override
	public void intervalRemoved(final ListDataEvent event) {
		fireTableRowsDeleted(event.getIndex0(), event.getIndex1());
		fireTableChanged(new TableModelEvent(this, event.getIndex0(), event.getIndex1(), TableModelEvent.ALL_COLUMNS,
				TABLE_CHANGE_DONE));
	}

	public T getObjectAtRow(final int row) {
		return model.getValueAt(row);
	}

	public int getRowOf(final T object) {
		return model.getRowOf(object);
	}

	@Override
	public String getColumnName(final int column) {
		return columnsEnum[column].name();
	}

	protected boolean verbose() {
		return false;
	}

	protected C columnOf(final int column) {
		return columnsEnumClass.cast(columnsEnum[column]);
	}

}
