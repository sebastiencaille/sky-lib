package ch.skymarshall.tcwriter.hmi.steps;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.skymarshall.hmi.mvc.properties.ObjectProperty;

import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.visitors.HumanReadableVisitor;

public class StepsCellRenderer extends DefaultTableCellRenderer {

	private HumanReadableVisitor summaryVisitor;

	public StepsCellRenderer(final ObjectProperty<TestCase> testCaseProperty) {
		super();
		testCaseProperty.addListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				summaryVisitor = new HumanReadableVisitor(testCaseProperty.getObjectValue());
			}
		});
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
