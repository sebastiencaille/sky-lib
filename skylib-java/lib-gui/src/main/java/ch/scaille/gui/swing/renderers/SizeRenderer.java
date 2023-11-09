package ch.scaille.gui.swing.renderers;

import java.awt.Component;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.scaille.util.text.FormatterHelper;

@SuppressWarnings("serial")
public class SizeRenderer extends DefaultTableCellRenderer {

	private final BiConsumer<Long, Component> componentTuning;

	public SizeRenderer(BiConsumer<Long, Component> componentTuning) {
		this.componentTuning = componentTuning;
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		final var size = Optional.ofNullable((Number) value);
		final var comp = super.getTableCellRendererComponent(table, size.map(FormatterHelper::toSize).orElse(null),
				isSelected, hasFocus, row, column);
		if (componentTuning != null) {
			componentTuning.accept(size.map(Number::longValue).orElse(-1L), comp);
		}
		return comp;
	}

}
