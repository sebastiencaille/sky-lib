package org.skymarshall.hmi.swing16.renderers;

import java.awt.Component;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof TemporalAccessor) {
            setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format((TemporalAccessor) value));
        } else {
            setText("");
        }
        return this;
    }

}
