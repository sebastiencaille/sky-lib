package ch.scaille.gui.swing.jtable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;

import ch.scaille.gui.swing.JTableHelper;
import ch.scaille.gui.swing.jtable.TableColumnWithPolicy.FixedTextLengthColumn;
import ch.scaille.gui.swing.jtable.TableColumnWithPolicy.FixedWidthColumn;
import ch.scaille.gui.swing.jtable.TableColumnWithPolicy.PercentOfAvailableSpaceColumn;
import ch.scaille.gui.swing.jtable.TableColumnWithPolicy.PercentOfTableWidthColumn;
import ch.scaille.gui.swing.model.ListModelTableModel;

/**
 * Column model that uses {@link TableColumnWithPolicy}.
 * <p>
 * The remaining space will be occupied by freePercentColumns.
 *
 * @author Sebastien Caille
 *
 * @param <C> enum that defines the columns (see {@link ListModelTableModel}
 */
public class PolicyTableColumnModel<C extends Enum<C>> extends DefaultTableColumnModel {

	private final List<Class<?>> policyExecutionOrder;

	private final JTable table;

	public static final List<Class<?>> DEFAULT_POLICIES = List.of(FixedWidthColumn.class,
			FixedTextLengthColumn.class, PercentOfTableWidthColumn.class, PercentOfAvailableSpaceColumn.class);

	public PolicyTableColumnModel(final JTable table) {
		this(table, DEFAULT_POLICIES);
	}

	protected PolicyTableColumnModel(final JTable table, final List<Class<?>> executionOrder) {
		this.table = table;
		this.policyExecutionOrder = executionOrder;
		table.addPropertyChangeListener("columnModel", p -> table.setAutoCreateColumnsFromModel(false));
	}

	public PolicyTableColumnModel<C> install() {
		table.setColumnModel(this);
		table.createDefaultColumnsFromModel();
		return this;
	}

	boolean isValid() {
		return table.isValid();
	}

	/**
	 * Updates the width of the columns
	 */
	void updateColumnsWidth() {

		// Computes the actual size of the columns
		final var width = table.getWidth();
		if (width == 0) {
			return;
		}

		// Classify columns per type
		var unallocatedWidth = width;
		final var columnPerClass = new HashMap<Class<?>, List<TableColumnWithPolicy<C>>>();
		final var columnsEnum = getColumns();
		while (columnsEnum.hasMoreElements()) {
			final var column = columnsEnum.nextElement();
			if (column instanceof TableColumnWithPolicy) {
				columnPerClass.computeIfAbsent(column.getClass(), v -> new ArrayList<>())
						.add((TableColumnWithPolicy<C>) column);
			} else {
				unallocatedWidth -= column.getWidth();
			}
		}

		// Apply each policy per class
		for (final var policyClass : policyExecutionOrder) {
			final var columnsOfPolicy = columnPerClass.get(policyClass);
			if (columnsOfPolicy == null) {
				// no policy
				continue;
			}
			final var info = new ColumnComputationInfo(table, columnsOfPolicy.size(), unallocatedWidth);
			for (final var column : columnsOfPolicy) {
				final var computedWidth = column.computeWidth(info);
				column.setComputedWidth(computedWidth);
				unallocatedWidth -= column.getWidth();
			}
		}

		fixWidthRounding(unallocatedWidth, columnPerClass);

		table.getTableHeader().repaint();
		table.repaint();
	}

	private void fixWidthRounding(final int unallocatedWidth,
			final Map<Class<?>, List<TableColumnWithPolicy<C>>> columnPerClass) {

		final var remainingPercentWidthCols = columnPerClass.get(PercentOfAvailableSpaceColumn.class);
		var remainingWidth = unallocatedWidth;

		if (remainingWidth < 0 && remainingPercentWidthCols != null && !remainingPercentWidthCols.isEmpty()) {
			final var perColumnCorrection = remainingWidth / remainingPercentWidthCols.size() - 1;
			remainingWidth -= perColumnCorrection * remainingPercentWidthCols.size();
			remainingPercentWidthCols.forEach(col -> col.setComputedWidth(col.getWidth() + perColumnCorrection));
		}

		if (remainingWidth > 0 && remainingPercentWidthCols != null && !remainingPercentWidthCols.isEmpty()) {
			final var percentSum = (Integer) remainingPercentWidthCols.stream().mapToInt(c -> ((PercentOfAvailableSpaceColumn<C>) c).getPercent()).sum();
			if (percentSum == 100) {
				final var lastPercentColumn = remainingPercentWidthCols.getLast();
				lastPercentColumn.setComputedWidth(lastPercentColumn.getWidth() + remainingWidth);
			}
		}

	}

	public void configureColumn(final TableColumnWithPolicy<C> column) {
		if (table.getColumnModel() != this) {
			throw new IllegalStateException("Table column model is not this model");
		}
		if (tableColumns.isEmpty()) {
			table.createDefaultColumnsFromModel();
		}
		final var index = JTableHelper.modelColumnIndex(table, column.getColumn());
		column.setModelIndex(index);
		final var old = tableColumns.get(index);
		tableColumns.removeElementAt(index);
		column.setHeaderRenderer(old.getHeaderRenderer());
		column.setHeaderValue(old.getHeaderValue());
		column.onUpdate(this::updateColumnsWidth);
		tableColumns.add(index, column);
		updateColumnsWidth();
	}

	@Override
	public int getColumnIndex(final Object identifier) {
		return ((C) identifier).ordinal();
	}

	@Override
	protected void recalcWidthCache() {
		updateColumnsWidth();
		super.recalcWidthCache();
	}

}
