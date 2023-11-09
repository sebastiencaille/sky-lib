package ch.scaille.gui.swing.renderers;

import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A percentage renderer.
 * <p>
 * 
 * @author Sebastien Caille
 *
 */
@SuppressWarnings("serial")
public class PercentRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		if (isSelected) {
			setBackground(table.getSelectionBackground());
		} else {
			setBackground(table.getBackground());
		}
		final var val = ((Number) value).floatValue();
		setText(NumberFormat.getPercentInstance().format(val));
		return this;
	}
}
