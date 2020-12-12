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
package ch.skymarshall.gui.swing.renderers;

import java.awt.Component;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.skymarshall.gui.Utils;

@SuppressWarnings("serial")
public class SizeRenderer extends DefaultTableCellRenderer {

	private final BiConsumer<Integer, Component> componentTuning;

	public SizeRenderer(BiConsumer<Integer, Component> componentTuning) {
		this.componentTuning = componentTuning;
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		Optional<Number> size = Optional.ofNullable((Number) value);
		Component comp = super.getTableCellRendererComponent(table, size.map(Utils::toSize).orElse(null), isSelected,
				hasFocus, row, column);
		if (componentTuning != null) {
			componentTuning.accept(size.map(Number::intValue).orElse(-1), comp);
		}
		return comp;
	}

}
