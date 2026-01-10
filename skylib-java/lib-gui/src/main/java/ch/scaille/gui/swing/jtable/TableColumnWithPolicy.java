package ch.scaille.gui.swing.jtable;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import ch.scaille.gui.swing.SwingExt;
import ch.scaille.gui.swing.model.ListModelTableModel;
import lombok.Getter;

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
	@Serial
	private static final long serialVersionUID = 6441064779321521655L;
	
	public static final Margin DEFAULT_MARGIN = Margin.px(5);
	public static final String SAMPLE_LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam vehicula lorem ut neque condimentum, non hendrerit nisl molestie. Morbi non vehicula erat. Phasellus nec diam quis ipsum lacinia congue id in nisi. Aenean dolor lectus, ornare ut faucibus nec, sagittis in mauris. Nulla ac bibendum sapien, quis porta nunc. Morbi sit amet metus massa. Vestibulum feugiat pretium enim, at maximus mi convallis eget. Duis maximus in nunc quis ornare. Duis dui risus, mattis in leo a, semper rutrum ante. Aliquam rutrum laoreet feugiat. Quisque rhoncus felis vitae metus volutpat finibus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed sed viverra ipsum. In hac habitasse platea dictumst. Pellentesque a purus diam. Nullam facilisis metus ut nulla dapibus finibus.";
	public static final String SAMPLE_NUMBERS = "0";

	private Runnable onUpdate;

	@Getter
    private final C column;

	protected int forcedWidth = 0;

	public abstract int computeWidth(ColumnComputationInfo policyInfo);

		
	protected TableColumnWithPolicy(final C column) {
		this.column = column;
		setIdentifier(column.name());
	}

    @Override
	public void setWidth(final int width) {
		forcedWidth = width;
		onUpdate.run();
	}

	public void onUpdate(Runnable onUpdate) {
		this.onUpdate = onUpdate;
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

		@Serial
		private static final long serialVersionUID = 9186866524593837361L;
		
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
	 */
	public static <C extends Enum<C>> TableColumnWithPolicy<C> fixedWidth(final C column, final int fixedWidth) {
		return new FixedWidthColumn<>(column, fixedWidth);
	}

	protected static class FixedTextLengthColumn<C extends Enum<C>> extends TableColumnWithPolicy<C> {

		@Serial
		private static final long serialVersionUID = 3704779792866427376L;

		private static final Map<String, Float> WIDTH_CACHE = new HashMap<>();

		private final int fixedTextLength;

		private final String sample;

		private final Margin margin;

		public FixedTextLengthColumn(final C column, final int fixedTextLength, String sample, Margin margin) {
			super(column);
			this.fixedTextLength = fixedTextLength;
			this.sample = sample;
			this.margin = margin;
		}

		@Override
		public int computeWidth(final ColumnComputationInfo policyInfo) {
			final var charRatio = WIDTH_CACHE.computeIfAbsent(sample + policyInfo.getFont().toString(),
					d -> ((float) SwingExt.computeTextWidth(policyInfo.table, sample)) / sample.length());
			final var columnWidth = (int) (charRatio * fixedTextLength);
			return columnWidth + margin.compute(columnWidth);
		}
	}

	/**
	 * Creates a column with a text based width, the width of a char being based on
	 * the lorem ipsum and the table's font
	 */
	public static <C extends Enum<C>> TableColumnWithPolicy<C> fixedTextLength(final C column,
																			   final int fixedTextLength) {
		return fixedTextLength(column, fixedTextLength, SAMPLE_LOREM_IPSUM, DEFAULT_MARGIN);
	}

	/**
	 * Creates a column with a text based width, the width of a char being based on
	 * sample and the table's font
	 * 
	 * @param <C> the column's enum
	 * @param column the column
	 * @param fixedTextLength the length of the text
	 * @param sample a sample used to compute the mean char width
	 * @return the column policy
	 */
	public static <C extends Enum<C>> TableColumnWithPolicy<C> fixedTextLength(final C column, final int fixedTextLength,
																			   String sample, Margin margins) {
		return new FixedTextLengthColumn<>(column, fixedTextLength, sample, margins);
	}

	protected static class PercentOfTableWidthColumn<C extends Enum<C>> extends TableColumnWithPolicy<C> {
		
		@Serial
		private static final long serialVersionUID = -6264204115324227837L;
		
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
	 */
	public static <C extends Enum<C>> TableColumnWithPolicy<C> percentOfTableWidth(final C column, final int percent) {
		return new PercentOfTableWidthColumn<>(column, percent);
	}

	@Getter
    protected static class PercentOfAvailableSpaceColumn<C extends Enum<C>> extends TableColumnWithPolicy<C> {
		
		@Serial
		private static final long serialVersionUID = 5672380476791184732L;
		
		private final int percent;

		public PercentOfAvailableSpaceColumn(final C column, final int percent) {
			super(column);
			this.percent = percent;
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
	 */
	public static <C extends Enum<C>> TableColumnWithPolicy<C> percentOfAvailableSpace(final C column,
			final int percent) {
		return new PercentOfAvailableSpaceColumn<>(column, percent);
	}

}
