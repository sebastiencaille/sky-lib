package ch.skymarshall.tcwriter.gui.steps;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.skymarshall.tcwriter.gui.TestRemoteControl.StepState;
import ch.skymarshall.tcwriter.gui.TestRemoteControl.StepStatus;

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
		if (value == null || !StepsTable.displayBreakPoint(row)) {
			jPanel.setBackground(table.getBackground());
			return jPanel;
		}
		final StepStatus status = (StepStatus) value;
		renderer.setSelected(status.breakPoint);

		if (status.state == StepState.STARTED) {
			renderer.setBackground(Color.CYAN);
		} else if (status.state == StepState.OK) {
			renderer.setBackground(Color.GREEN.darker());
		} else if (status.state == StepState.FAILED) {
			renderer.setBackground(Color.RED.darker());
		} else {
			renderer.setBackground(table.getBackground());
		}
		if (status.message != null) {
			renderer.setToolTipText(status.message);
		}
		return renderer;
	}

}
