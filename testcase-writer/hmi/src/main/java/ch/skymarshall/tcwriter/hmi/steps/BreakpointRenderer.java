package ch.skymarshall.tcwriter.hmi.steps;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.skymarshall.tcwriter.hmi.TestRemoteControl;

public class BreakpointRenderer extends DefaultTableCellRenderer {

	private final TestRemoteControl testControl;
	final JCheckBox renderer = new JCheckBox();

	public BreakpointRenderer(final TestRemoteControl testControl) {
		this.testControl = testControl;
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		renderer.setSelected(value != null && (Boolean) value);
		if (testControl.isRunning(row + 1)) {
			renderer.setBackground(Color.CYAN);
		} else {
			renderer.setBackground(table.getBackground());
		}
		return renderer;
	}

}
