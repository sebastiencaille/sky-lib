package ch.scaille.tcwriter.gui.steps;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.scaille.tcwriter.testexec.model.StepStatus;

public class StepStatusRenderer extends DefaultTableCellRenderer {

	final JCheckBox renderer = new JCheckBox();
	final JPanel jPanel = new JPanel();

	public StepStatusRenderer() {
		renderer.setText("");
		renderer.setToolTipText("");
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		if (value == null) {
			jPanel.setBackground(table.getBackground());
			return jPanel;
		}
		final var status = (StepStatus) value;
		renderer.setSelected(status.breakPoint);
		renderer.setBackground(switch (status.state) {
			case STARTED -> Color.CYAN;
			case OK -> Color.GREEN.darker();
			case FAILED -> Color.RED.darker();
			default -> table.getBackground();
		});
		if (status.message != null) {
			renderer.setToolTipText(status.message);
		}
		return renderer;
	}

}
