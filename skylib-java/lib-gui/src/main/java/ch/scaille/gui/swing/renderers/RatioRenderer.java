package ch.scaille.gui.swing.renderers;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.JTable;

@SuppressWarnings("serial")
public class RatioRenderer extends PercentRenderer {

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		final var val = ((Number) value).floatValue();
		if (!isSelected && val > 0) {
			setBackground(Color.cyan.brighter());
		}

		setText(NumberFormat.getPercentInstance().format(value));
		return this;
	}
}
