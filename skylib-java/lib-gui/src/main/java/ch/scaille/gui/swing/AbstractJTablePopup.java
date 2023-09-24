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
package ch.scaille.gui.swing;

import java.awt.Point;
import java.util.Collection;

import javax.swing.JTable;

import ch.scaille.gui.mvc.properties.ObjectProperty;
import ch.scaille.gui.swing.model.ListModelTableModel;

/**
 * Popup on JTable with a ListModelTableModel
 * <p>
 * Provides object at the line the popup was opened.
 * 
 * @author Sebastien Caille
 * 
 * @param <T>
 */
public abstract class AbstractJTablePopup<T> extends AbstractPopup<T> {

	private final JTable table;
	private final ListModelTableModel<T, ?> model;
	private final ObjectProperty<? extends Collection<T>> selections;

	protected AbstractJTablePopup(final JTable table, final ListModelTableModel<T, ?> model,
			final ObjectProperty<T> lastSelected, final ObjectProperty<? extends Collection<T>> selections) {
		super(lastSelected);
		this.table = table;
		this.model = model;
		this.selections = selections;
	}

	@Override
	protected T getValueForPopup(final Point p) {
		final var objectToSelect = model.getObjectAtRow(table.rowAtPoint(p));
		if (objectToSelect != null) {
			var selected = selections != null && selections.getValue() != null
					&& selections.getValue().contains(objectToSelect);
			selected |= lastSelected != null && lastSelected.getValue() == objectToSelect;

			if (!selected) {
				final var index = model.getRowOf(objectToSelect);
				table.getSelectionModel().setSelectionInterval(index, index);
			}
		}
		return objectToSelect;
	}

}
