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

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.swing.model.ListModelTableModel;

public class SwingBindings {

	public static <T, C extends JComponent> IComponentBinding<T> rw(final C component,
			final Consumer<IComponentLink<T>> readerListener, final Consumer<T> writer, final T defaultValue) {
		return new IComponentBinding<T>() {
			@Override
			public void setComponentValue(final AbstractProperty source, final T value) {
				writer.accept(value != null ? value : defaultValue);
			}

			@Override
			public void addComponentValueChangeListener(final IComponentLink<T> converter) {
				readerListener.accept(converter);
			}
		};
	}

	public static String textOrNull(final String val) {
		return val != null ? val : "<null>";
	}

	public static <C extends ItemSelectable, T> Consumer<IComponentLink<T>> itemListener(final C component,
			final Function<ItemEvent, T> reader, final Function<ItemEvent, Boolean> activator) {
		return new Consumer<IComponentLink<T>>() {
			@Override
			public void accept(final IComponentLink<T> l) {
				component.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(final ItemEvent e) {
						if (activator.apply(e)) {
							l.setValueFromComponent(component, reader.apply(e));
						}
					}
				});
			}
		};
	}

	public static IComponentBinding<Boolean> value(final JCheckBox cb) {
		return rw(cb, itemListener(cb, e -> e.getStateChange() == ItemEvent.SELECTED, (e) -> true), cb::setSelected,
				false);
	}

	public static JTextFieldBinding value(final JTextField component) {
		return new JTextFieldBinding(component);
	}

	public static IComponentBinding<String> value(final JTextArea component, final boolean readOnly) {
		return new JTextAreaBinding(component, readOnly);
	}

	public static <T> IComponentBinding<T> selection(final JList<T> editor, final Class<T> contentType) {
		return new JListSelectionBinding<>(editor, contentType);
	}

	public static <T> IComponentBinding<List<T>> values(final JList<T> editor) {
		return new JListContentBinding<>(editor);
	}

	public static <T> IComponentBinding<T> selection(final JTable editor, final ListModelTableModel<T, ?> tableModel) {
		return new JTableSelectionBinding<>(editor, tableModel);
	}

	public static <T> IComponentBinding<List<T>> multipleSelection(final JTable editor,
			final ListModelTableModel<T, ?> tableModel) {
		return new JTableMultiSelectionBinding<>(editor, tableModel);
	}

	public static <T> IComponentBinding<List<T>> values(final JComboBox<T> component) {
		return new JComboBoxValuesBinding<>(component);
	}

	public static <T> IComponentBinding<T> selection(final JComboBox<T> component) {
		return rw(component,
				itemListener(component, e -> (T) e.getItem(), e -> e.getStateChange() == ItemEvent.SELECTED),
				component::setSelectedItem, null);
	}

}
