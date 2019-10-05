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
package ch.skymarshall.gui.swing.bindings;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.gui.swing.model.ListModelTableModel;

public interface SwingBindings {

	/**
	 *
	 * @author scaille
	 *
	 * @param <T> incoming data type
	 * @param <C> component type
	 * @param <L> listener type
	 */
	public static class ListenerRegistration<T, C, L> {
		private final BiFunction<IComponentLink<T>, C, L> listenerFactory;
		private final BiConsumer<C, L> addListener;
		private final BiConsumer<C, L> removeListener;
		private L listener;

		public ListenerRegistration(final BiFunction<IComponentLink<T>, C, L> listenerFactory,
				final BiConsumer<C, L> addListener, final BiConsumer<C, L> removeListener) {
			this.listenerFactory = listenerFactory;
			this.addListener = addListener;
			this.removeListener = removeListener;
		}

		public void addListener(final C component, final IComponentLink<T> toProperty) {
			if (listener != null) {
				throw new IllegalStateException("Listener already added");
			}
			listener = listenerFactory.apply(toProperty, component);
			addListener.accept(component, listener);
		}

		public void removeListener(final C component) {
			removeListener.accept(component, listener);
		}

	}

	/**
	 *
	 * @param component               an awt component
	 * @param componentReaderListener the listener registration that propagate value
	 *                                changed by the component
	 * @param componentWriter         the consumer that sets the value of the
	 *                                component based on the incoming value
	 * @param defaultValue            a default value for null incoming value
	 * @return
	 */
	public static <T, C extends JComponent> IComponentBinding<T> rw(final C component,
			final ListenerRegistration<T, C, ?> componentReaderListener, final Consumer<T> componentWriter,
			final T defaultValue) {
		return new IComponentBinding<T>() {
			@Override
			public void setComponentValue(final AbstractProperty source, final T value) {
				componentWriter.accept(value != null ? value : defaultValue);
			}

			@Override
			public void addComponentValueChangeListener(final IComponentLink<T> converter) {
				componentReaderListener.addListener(component, converter);
			}

			@Override
			public void removeComponentValueChangeListener() {
				componentReaderListener.removeListener(component);
			}
		};
	}

	public static String textOrNull(final String val) {
		return val != null ? val : "<null>";
	}

	public static <C extends ItemSelectable, T> ListenerRegistration<T, C, ?> itemListener(
			final Function<ItemEvent, Boolean> activator, final Function<ItemEvent, T> valueExtractor) {
		return new ListenerRegistration<T, C, ItemListener>((link, component) -> {
			return event -> {
				if (activator.apply(event)) {
					link.setValueFromComponent(component, valueExtractor.apply(event));
				}
			};
		}, (c, l) -> c.addItemListener(l), (c, l) -> c.removeItemListener(l));
	}

	public static IComponentBinding<Boolean> selected(final JCheckBox cb) {
		return rw(cb, itemListener(e -> true, e -> e.getStateChange() == ItemEvent.SELECTED), cb::setSelected, false);
	}

	public static JTextFieldBinding value(final JTextField component) {
		return new JTextFieldBinding(component);
	}

	public static IComponentBinding<String> value(final JTextArea component, final boolean readOnly) {
		return new JTextAreaBinding(component, readOnly);
	}

	public static <T> IComponentBinding<T> selection(final JList<T> editor) {
		return new JListSelectionBinding<>(editor);
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
		return rw(component, itemListener(e -> e.getStateChange() == ItemEvent.SELECTED, e -> (T) e.getItem()),
				component::setSelectedItem, null);
	}

}
