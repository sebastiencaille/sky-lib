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
package ch.skymarshall.gui.swing.jtable;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import ch.skymarshall.gui.swing.model.ListModelTableModel;

/**
 * Table column model based on per-column contribution.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <C> enum that defines the columns (see {@link ListModelTableModel}
 */
public abstract class TableColumnWithPolicy<C extends Enum<C>> extends TableColumn {

	private PolicyTableColumnModel<C> model;

	private final C column;

	protected int forcedWidth = 0;

	public abstract int computeWidth(ColumnComputationInfo policyInfo);

	public TableColumnWithPolicy(final C column) {
		this.column = column;
	}

	public C getColumn() {
		return column;
	}

	@Override
	public void setWidth(final int width) {
		forcedWidth = width;
		model.update();
	}

	public void setModel(final PolicyTableColumnModel<C> model) {
		this.model = model;
	}

	public void setComputedWidth(final int width) {
		super.setWidth(width);
	}

	public TableColumnWithPolicy<C> apply(final TableCellRenderer renderer) {
		setCellRenderer(renderer);
		return this;
	}

	public TableColumnWithPolicy<C> apply(final TableCellRenderer renderer, final TableCellEditor editor) {
		setCellRenderer(renderer);
		setCellEditor(editor);
		return this;
	}

	protected static class FixedWidthColumn<C extends Enum<C>> extends TableColumnWithPolicy<C> {

		private final int fixedWidth;

		public FixedWidthColumn(final C column, final int fixedWidth) {
			super(column);
			this.fixedWidth = fixedWidth;
		}

		@Override
		public int computeWidth(final ColumnComputationInfo policyInfo) {
			return fixedWidth;
		}
	}

	public static <C extends Enum<C>> TableColumnWithPolicy<C> fixedWidth(final C column, final int fixedWidth) {
		return new FixedWidthColumn<>(column, fixedWidth);
	}

	protected static class PercentOfTableWidthColumn<C extends Enum<C>> extends TableColumnWithPolicy<C> {
		private final int percent;

		public PercentOfTableWidthColumn(final C column, final int percent) {
			super(column);
			this.percent = percent;
		}

		@Override
		public int computeWidth(final ColumnComputationInfo policyInfo) {
			if (forcedWidth > 0) {
				return forcedWidth;
			}
			return policyInfo.tableWidth * percent / 100;
		}

	}

	public static <C extends Enum<C>> TableColumnWithPolicy<C> percentOfTableWidth(final C column, final int percent) {
		return new PercentOfTableWidthColumn<>(column, percent);
	}

	protected static class PercentOfAvailableSpaceColumn<C extends Enum<C>> extends TableColumnWithPolicy<C> {
		private final int percent;

		public PercentOfAvailableSpaceColumn(final C column, final int percent) {
			super(column);
			this.percent = percent;
		}

		public int getPercent() {
			return percent;
		}

		@Override
		public int computeWidth(final ColumnComputationInfo policyInfo) {
			if (forcedWidth > 0) {
				return forcedWidth;
			}
			return policyInfo.unallocatedWidth * percent / 100;
		}
	}

	public static <C extends Enum<C>> TableColumnWithPolicy<C> percentOfAvailableSpace(final C column,
			final int percent) {
		return new PercentOfAvailableSpaceColumn<>(column, percent);
	}

}
