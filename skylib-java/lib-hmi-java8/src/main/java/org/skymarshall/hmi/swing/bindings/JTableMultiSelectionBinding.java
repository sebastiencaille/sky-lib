/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.swing.model.ListModelTableModel;

/**
 * Binds to multiple selection of JTable.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class JTableMultiSelectionBinding<T> extends DefaultComponentBinding<List<T>> {

	private final JTable table;
	private final ListModelTableModel<T, ?> model;
	private boolean modelChange = false;

	public JTableMultiSelectionBinding(final JTable component, final ListModelTableModel<T, ?> model) {
		this.table = component;
		this.model = model;
		table.setModel(model);
	}

	private void updateSelection(final IComponentLink<List<T>> converter) {
		final List<T> selected = new ArrayList<>();
		for (final int row : table.getSelectedRows()) {
			if (row >= 0 && row < model.getRowCount()) {
				selected.add(model.getObjectAtRow(row));
			}
		}
		converter.setValueFromComponent(table, selected);
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<List<T>> converter) {
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting() && !modelChange) {
					updateSelection(converter);
				}
			}

		});
		model.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(final TableModelEvent event) {
				if (event.getType() == ListModelTableModel.TABLE_CHANGE_DONE) {
					modelChange = false;
					converter.reloadComponentValue();
				} else if (event.getType() == ListModelTableModel.TABLE_ABOUT_TO_CHANGE) {
					modelChange = true;
				} else if (!modelChange) {
					updateSelection(converter);
				}
			}
		});

	}

	@Override
	public void setComponentValue(final AbstractProperty source, final List<T> values) {
		if ((source == null || !source.isModifiedBy(table)) && values != null) {

			table.getSelectionModel().setValueIsAdjusting(true);
			table.getSelectionModel().clearSelection();

			for (final T value : values) {
				final int index = model.getRowOf(value);
				if (index >= 0) {
					table.getSelectionModel().addSelectionInterval(index, index);
				}
			}
			table.getSelectionModel().setValueIsAdjusting(false);
		}
	}
}
