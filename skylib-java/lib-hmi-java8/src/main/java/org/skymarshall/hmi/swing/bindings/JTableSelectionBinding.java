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
package org.skymarshall.hmi.swing.bindings;

import javax.swing.JTable;

import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.swing.model.ListModelTableModel;

public class JTableSelectionBinding<T> extends DefaultComponentBinding<T> {

	private final JTable table;
	private final ListModelTableModel<T, ?> model;

	private boolean modelChange = false;
	private IComponentLink<T> converter;

	public JTableSelectionBinding(final JTable component, final ListModelTableModel<T, ?> model) {
		this.table = component;
		this.model = model;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<T> converter) {
		this.converter = converter;
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && !modelChange) {
				updateSelection(converter);
			}
		});

		model.addTableModelListener(event -> {
			if (event.getType() == ListModelTableModel.TABLE_CHANGE_DONE) {
				modelChange = false;
				converter.reloadComponentValue();
			} else if (event.getType() == ListModelTableModel.TABLE_ABOUT_TO_CHANGE) {
				modelChange = true;
			}
		});

	}

	@Override
	public void setComponentValue(final AbstractProperty source, final T value) {
		if (source == null || !source.isModifiedBy(table)) {
			if (value == null) {
				table.getSelectionModel().clearSelection();
			} else {
				final int index = model.getRowOf(value);
				if (table.getSelectedRow() != index && index >= 0) {
					table.getSelectionModel().setSelectionInterval(index, index);
				}
				if (index < 0) {
					converter.setValueFromComponent(this, null);
				}
			}
		}
	}

	protected void updateSelection(final IComponentLink<T> converter) {
		final int selectedRow = table.getSelectedRow();
		if (selectedRow >= 0) {
			final T object = model.getObjectAtRow(selectedRow);
			converter.setValueFromComponent(table, object);
		} else {
			converter.setValueFromComponent(table, null);
		}
	}

}
