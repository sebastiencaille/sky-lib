package ch.skymarshall.tcwriter.hmi.steps;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.skymarshall.tcwriter.hmi.TestRemoteControl.StepState;
import ch.skymarshall.tcwriter.hmi.TestRemoteControl.StepStatus;

public class StepStatusRenderer extends DefaultTableCellRenderer {

	final JCheckBox renderer = new JCheckBox();

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		final StepStatus status = (StepStatus) value;
		renderer.setSelected(status != null && status.breakPoint);

		if (status != null && status.state == StepState.STARTED) {
			renderer.setBackground(Color.CYAN);
		} else if (status != null && status.state == StepState.OK) {
			renderer.setBackground(Color.GREEN.darker());
		} else if (status != null && status.state == StepState.FAILED) {
			renderer.setBackground(Color.RED.darker());
		} else {
			renderer.setBackground(table.getBackground());
		}
		if (status != null && status.message != null) {
			renderer.setToolTipText(status.message);
		}
		return renderer;
	}

}
