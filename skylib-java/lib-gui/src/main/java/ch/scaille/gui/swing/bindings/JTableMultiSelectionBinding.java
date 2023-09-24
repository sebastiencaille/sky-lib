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
package ch.scaille.gui.swing.bindings;

import java.util.Collection;
import java.util.function.Supplier;

import javax.swing.JTable;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.gui.mvc.IComponentLink;
import ch.scaille.gui.mvc.properties.AbstractProperty;
import ch.scaille.gui.swing.factories.SwingBindings;
import ch.scaille.gui.swing.model.ListModelTableModel;

/**
 * Binds to multiple selection of JTable.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class JTableMultiSelectionBinding<T, U extends Collection<T>> extends ComponentBindingAdapter<U> {

	private final JTable table;
	private final ListModelTableModel<T, ?> model;
	private boolean modelChange = false;
	private final Supplier<U> collectionType;

	public JTableMultiSelectionBinding(final JTable component, final ListModelTableModel<T, ?> model,
			Supplier<U> collectionType) {
		this.table = component;
		this.model = model;
		this.collectionType = collectionType;
		table.setModel(model);
	}

	private void updateSelection(final IComponentLink<U> componentlink) {
		final var selected = collectionType.get();

		for (final var row : table.getSelectedRows()) {
			if (row >= 0 && row < model.getRowCount()) {
				selected.add(model.getObjectAtRow(row));
			}
		}
		componentlink.setValueFromComponent(table, selected);
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<U> componentlink) {
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && !modelChange) {
				updateSelection(componentlink);
			}
		});
		model.addTableModelListener(event -> {
			if (event.getType() == ListModelTableModel.TABLE_CHANGE_DONE) {
				modelChange = false;
				componentlink.reloadComponentValue();
			} else if (event.getType() == ListModelTableModel.TABLE_ABOUT_TO_CHANGE) {
				modelChange = true;
			}
		});

	}

	@Override
	public void setComponentValue(final AbstractProperty source, final U values) {
		if ((source == null || !source.isModifiedBy(table)) && values != null) {

			table.getSelectionModel().setValueIsAdjusting(true);
			table.getSelectionModel().clearSelection();

			for (final var value : values) {
				final var index = model.getRowOf(value);
				if (index >= 0) {
					table.getSelectionModel().addSelectionInterval(index, index);
				}
			}
			table.getSelectionModel().setValueIsAdjusting(false);
		}
	}

	@Override
	public String toString() {
		return "Multi-selection of " + SwingBindings.nameOf(table);
	}
}
