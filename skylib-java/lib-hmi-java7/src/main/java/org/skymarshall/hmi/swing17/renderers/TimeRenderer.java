package org.skymarshall.hmi.swing17.renderers;

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

    private final LocalDateTime aDay = LocalDate.now().atStartOfDay();

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof TemporalAccessor) {
            setText(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format((TemporalAccessor) value));
        } else if (value instanceof Duration) {
            setText(DateTimeFormatter.ofPattern("HH:mm").format(((Duration) value).addTo(aDay)));
        } else {
            setText("");
        }
        return this;
    }

}
