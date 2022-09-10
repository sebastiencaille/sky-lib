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
package ch.scaille.gui.swing.jtable;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import ch.scaille.gui.swing.SwingHelper;
import ch.scaille.gui.swing.model.ListModelTableModel;

/**
 * Table column model based on per-column contribution.
 * <p>
 * Classes are registered into PolicyTableColumnModel.DEFAULT_POLICIES
 *
 * @author Sebastien Caille
 *
 * @param <C> enum that defines the columns (see {@link ListModelTableModel}
 */
public abstract class TableColumnWithPolicy<C extends Enum<C>> extends TableColumn {

	public static final String SAMPLE_LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam vehicula lorem ut neque condimentum, non hendrerit nisl molestie. Morbi non vehicula erat. Phasellus nec diam quis ipsum lacinia congue id in nisi. Aenean dolor lectus, ornare ut faucibus nec, sagittis in mauris. Nulla ac bibendum sapien, quis porta nunc. Morbi sit amet metus massa. Vestibulum feugiat pretium enim, at maximus mi convallis eget. Duis maximus in nunc quis ornare. Duis dui risus, mattis in leo a, semper rutrum ante. Aliquam rutrum laoreet feugiat. Quisque rhoncus felis vitae metus volutpat finibus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed sed viverra ipsum. In hac habitasse platea dictumst. Pellentesque a purus diam. Nullam facilisis metus ut nulla dapibus finibus.";
	public static final String SAMPLE_NUMBERS = "0";

	private PolicyTableColumnModel<C> model;

	private final C column;

	protected int forcedWidth = 0;

	public abstract int computeWidth(ColumnComputationInfo policyInfo);

	protected TableColumnWithPolicy(final C column) {
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

	public TableColumnWithPolicy<C> with(final TableCellRenderer renderer) {
		setCellRenderer(renderer);
		return this;
	}

	public TableColumnWithPolicy<C> with(final TableCellRenderer renderer, final TableCellEditor editor) {
		setCellRenderer(renderer);
		setCellEditor(editor);
		return this;
	}

	@Override
	public String toString() {
		return column.name() + '-' + getClass().getSimpleName();
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

	/**
	 * Creates a column with a pixel based width
	 * 
	 * @param <C>
	 * @param column
	 * @param fixedWidth
	 * @return
	 */
	public static <C extends Enum<C>> TableColumnWithPolicy<C> fixedWidth(final C column, final int fixedWidth) {
		return new FixedWidthColumn<>(column, fixedWidth);
	}

	protected static class FixedTextWidthColumn<C extends Enum<C>> extends TableColumnWithPolicy<C> {

		private static Map<String, Float> WIDTH_CACHE = new HashMap<>();

		private final int fixedTextWidth;

		private final String sample;

		private final int margins;

		public FixedTextWidthColumn(final C column, final int fixedTextWidth, String sample, int margins) {
			super(column);
			this.fixedTextWidth = fixedTextWidth;
			this.sample = sample;
			this.margins = margins;
		}

		@Override
		public int computeWidth(final ColumnComputationInfo policyInfo) {
			float charRatio = WIDTH_CACHE.computeIfAbsent(sample + policyInfo.getFont().toString(),
					d -> ((float) SwingHelper.computeTextWidth(policyInfo.table, sample)) / sample.length());
			return (int) (charRatio * fixedTextWidth) + margins;
		}
	}

	/**
	 * Creates a column with a text based width, the width of a char being based on
	 * the lorem ipsum and the table's font
	 * 
	 * @param <C>
	 * @param column
	 * @param fixedTextWidth
	 * @return
	 */
	public static <C extends Enum<C>> TableColumnWithPolicy<C> fixedTextWidth(final C column,
			final int fixedTextWidth) {
		return fixedTextWidth(column, fixedTextWidth, SAMPLE_LOREM_IPSUM, 2);
	}

	/**
	 * Creates a column with a text based width, the width of a char being based on
	 * referenceText and the table's font
	 * 
	 * @param <C>
	 * @param column
	 * @param fixedTextWidth
	 * @param referenceText
	 * @return
	 */
	public static <C extends Enum<C>> TableColumnWithPolicy<C> fixedTextWidth(final C column, final int fixedTextWidth,
			String sample, int margins) {
		return new FixedTextWidthColumn<>(column, fixedTextWidth, sample, margins);
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

	/**
	 * Creates a column based on a fraction of the table's width
	 * 
	 * @param <C>
	 * @param column
	 * @param percent
	 * @return
	 */
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

	/**
	 * Creates a column based on a fraction of the unused space (that is, not
	 * allocated by another policy)
	 * 
	 * @param <C>
	 * @param column
	 * @param percent
	 * @return
	 */
	public static <C extends Enum<C>> TableColumnWithPolicy<C> percentOfAvailableSpace(final C column,
			final int percent) {
		return new PercentOfAvailableSpaceColumn<>(column, percent);
	}

}
