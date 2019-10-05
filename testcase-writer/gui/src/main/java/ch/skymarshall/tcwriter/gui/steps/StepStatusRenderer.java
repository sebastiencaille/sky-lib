package ch.skymarshall.tcwriter.gui.steps;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.skymarshall.tcwriter.gui.TestRemoteControl.StepState;
import ch.skymarshall.tcwriter.gui.TestRemoteControl.StepStatus;

public class StepStatusRenderer extends DefaultTableCellRenderer {

	final JCheckBox renderer = new JCheckBox();

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		if (value == null || row % 2 == 0) {
			renderer.setText("");
			renderer.setToolTipText("");
			renderer.setBackground(StepsTable.HUMAN_READABLE_BG_COLOR);
			return renderer;
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
