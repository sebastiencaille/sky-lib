package ch.scaille.gui.swing.renderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class SpeedRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		var val = ((Number) value).floatValue();

		if (isSelected) {
			setBackground(table.getSelectionBackground());
		} else if (val > 0.0) {
			setBackground(Color.CYAN.brighter());
		} else {
			setBackground(table.getBackground());
		}

		var unit = "";
		if (val > 1024) {
			unit = "Ko/s";
			val = val / 1024;
		}
		if (val > 1024) {
			unit = "Mo/s";
			val = val / 1024;
		}
		if (val > 1024) {
			unit = "Go/s";
			val = val / 1024;
		}
		var strVal = Float.toString(val);
		final var dotIndex = strVal.indexOf('.');
		if (strVal.length() > dotIndex + 2) {
			strVal = strVal.substring(0, dotIndex + 2);
		}
		return super.getTableCellRendererComponent(table, strVal + unit, isSelected, hasFocus, row, column);
	}

}
