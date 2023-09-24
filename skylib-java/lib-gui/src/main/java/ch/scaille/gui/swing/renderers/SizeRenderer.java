/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.scaille.gui.swing.renderers;

import java.awt.Component;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.scaille.util.FormatterHelper;

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
