package ch.scaille.gui.swing.renderers;

import java.awt.Component;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		final TemporalAccessor accessor;
		if (value instanceof TemporalAccessor temporalValue) {
			accessor = temporalValue;
		} else if (value instanceof Date dateValue) {
			accessor = LocalDateTime.ofInstant(dateValue.toInstant(), ZoneId.systemDefault());
		} else if (value instanceof Long longValue) {
			accessor = LocalDateTime.ofInstant(Instant.ofEpochMilli(longValue), ZoneId.systemDefault());
		} else {
			accessor = null;
		}

		if (accessor != null) {
			setText(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(accessor));
		} else {
			setText("");
		}
		return this;
	}

}
