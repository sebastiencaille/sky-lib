package ch.skymarshall.tcwriter.hmi.steps;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.skymarshall.tcwriter.hmi.TestRemoteControl;
import ch.skymarshall.tcwriter.hmi.TestRemoteControl.StepState;
import ch.skymarshall.tcwriter.hmi.TestRemoteControl.StepStatus;

public class BreakpointRenderer extends DefaultTableCellRenderer {

	private final TestRemoteControl testControl;
	final JCheckBox renderer = new JCheckBox();

	public BreakpointRenderer(final TestRemoteControl testControl) {
		this.testControl = testControl;
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		final StepStatus status = (StepStatus) value;
		renderer.setSelected(status != null && status.breakPoint);

		if (status != null && status.state == StepState.STARTED) {
			renderer.setBackground(Color.CYAN);
		} else if (status != null && status.state == StepState.OK) {
			renderer.setBackground(Color.GREEN.darker());
		} else {
			renderer.setBackground(table.getBackground());
		}
		return renderer;
	}

}
