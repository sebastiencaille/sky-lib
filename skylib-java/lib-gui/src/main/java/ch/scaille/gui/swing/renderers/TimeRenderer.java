package ch.scaille.gui.swing.renderers;

import java.awt.Component;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TimeRenderer extends DefaultTableCellRenderer {

	private static final DateTimeFormatter SHORT_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
	
	private final LocalDateTime aDay = LocalDate.now().atStartOfDay();

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		String text;
		if (value instanceof TemporalAccessor) {
			text = SHORT_FORMATTER.format((TemporalAccessor) value);
		} else if (value instanceof Duration) {
			text = TIME_FORMATTER.format(((Duration) value).addTo(aDay));
		} else {
			text = "";
		}
		setText(text);
		return this;
	}

}
