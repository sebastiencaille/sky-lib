package ch.skymarshall.tcwriter.hmi.steps;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.skymarshall.hmi.mvc.properties.ObjectProperty;

import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.visitors.HumanReadableVisitor;

public class StepsCellRenderer extends DefaultTableCellRenderer {

	private HumanReadableVisitor summaryVisitor;

	private final JPanel noRendering = new JPanel();

	public StepsCellRenderer(final ObjectProperty<TestCase> testCaseProperty) {
		super();
		testCaseProperty
				.addListener(evt -> summaryVisitor = new HumanReadableVisitor(testCaseProperty.getObjectValue()));
		noRendering.setSize(0, 0);
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {

		if (row % 2 == 0) {
			return noRendering;
		}

		final Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
				column);

		((JComponent) rendererComponent)
				.setToolTipText(summaryVisitor.process(((StepsTableModel) table.getModel()).getObjectAtRow(row / 2)));
		return rendererComponent;
	}

}
