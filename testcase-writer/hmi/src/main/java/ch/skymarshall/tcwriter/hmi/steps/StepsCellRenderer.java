package ch.skymarshall.tcwriter.hmi.steps;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.skymarshall.tcwriter.generators.TestSummaryVisitor;

public class StepsCellRenderer extends DefaultTableCellRenderer {

	private final TestSummaryVisitor summaryVisitor;

	public StepsCellRenderer(final TestSummaryVisitor summaryVisitor) {
		super();
		this.summaryVisitor = summaryVisitor;
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		final Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
				column);
		((JComponent) rendererComponent)
				.setToolTipText(summaryVisitor.process(((StepsTableModel) table.getModel()).getObjectAtRow(row)));
		return rendererComponent;
	}

}
