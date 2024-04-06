package ch.scaille.gui.swing.factories;

import java.awt.Component;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ch.scaille.gui.swing.bindings.JComboBoxContentBinding;
import ch.scaille.gui.swing.bindings.JListContentBinding;
import ch.scaille.gui.swing.bindings.JListSelectionBinding;
import ch.scaille.gui.swing.bindings.JSpinnerBinding;
import ch.scaille.gui.swing.bindings.JTableMultiSelectionBinding;
import ch.scaille.gui.swing.bindings.JTableSelectionBinding;
import ch.scaille.gui.swing.bindings.JTextAreaBinding;
import ch.scaille.gui.swing.bindings.JTextFieldBinding;
import ch.scaille.gui.swing.model.ListModelTableModel;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.properties.AbstractProperty;

public class SwingBindings {

	private SwingBindings() {
	}

	public static String nameOf(final Component component) {
		if (component.getName() != null) {
			return component.getClass().getSimpleName() + ':' + component.getName();
		}
		return component.toString();
	}

	/**
	 * Class that contains all listener life cycle (create, add, remove)
	 **/
	private static class ListenerRegistration<T, C, L> {
		private final BiFunction<IComponentLink<T>, C, L> createListener;
		private final BiConsumer<C, L> addListener;
		private final BiConsumer<C, L> removeListener;
		private L listener;

		public ListenerRegistration(final BiFunction<IComponentLink<T>, C, L> createListener,
				final BiConsumer<C, L> addListener, final BiConsumer<C, L> removeListener) {
			this.createListener = createListener;
			this.addListener = addListener;
			this.removeListener = removeListener;
		}

		public void addListener(final C component, final IComponentLink<T> toProperty) {
			if (listener != null) {
				throw new IllegalStateException("Listener already added");
			}
			listener = createListener.apply(toProperty, component);
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
		return new IComponentBinding<>() {
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

			@Override
			public String toString() {
				return "Binding to " + nameOf(component);
			}
		};
	}

	public static String textOrNull(final String val) {
		return val != null ? val : "<null>";
	}

	/**
	 * Conditionally listen to an item and converts its value
	 * @param <C> the ItemSelectable type
	 * @param <T> the converted type
	 * @param activator the condition that allows the value propagation 
	 * @param converter the converter from the item value to the listener value
	 * @return a listener registration
	 */
	public static <C extends ItemSelectable, T> ListenerRegistration<T, C, ?> itemListener(
			final Predicate<ItemEvent> activator, final Function<ItemEvent, T> converter) {
		return new ListenerRegistration<>((link, component) -> event -> {
			if (activator.test(event)) {
				link.setValueFromComponent(component, converter.apply(event));
			}
		}, ItemSelectable::addItemListener, ItemSelectable::removeItemListener);
	}

	public static IComponentBinding<Boolean> selected(final JCheckBox cb) {
		return rw(cb, itemListener(e -> true, e -> e.getStateChange() == ItemEvent.SELECTED), cb::setSelected, false);
	}

	public static JTextFieldBinding value(final JTextField component) {
		return new JTextFieldBinding(component);
	}

	public static <T extends Number> JSpinnerBinding<T> value(final JSpinner component) {
		return new JSpinnerBinding<>(component);
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

	public static <T, U extends Collection<T>> IComponentBinding<U> multipleSelection(final JTable editor,
			final ListModelTableModel<T, ?> tableModel, Supplier<U> collectionSupplier) {
		return new JTableMultiSelectionBinding<>(editor, tableModel, collectionSupplier);
	}

	public static <T> IComponentBinding<List<T>> multipleSelection(final JTable editor,
			final ListModelTableModel<T, ?> tableModel) {
		return new JTableMultiSelectionBinding<>(editor, tableModel, ArrayList::new);
	}

	public static <T, U extends Collection<T>> IComponentBinding<U> values(final JComboBox<T> component) {
		return new JComboBoxContentBinding<>(component);
	}

	public static <T> IComponentBinding<T> selection(final JComboBox<T> component) {
		return rw(component, itemListener(e -> e.getStateChange() == ItemEvent.SELECTED, e -> (T) e.getItem()),
				component::setSelectedItem, null);
	}

	private static final class ActionListenerImplementation<T> implements ActionListener {
		private final IComponentLink<T> link;
		private final Object[] mapping;
		private final ButtonGroup group;

		private ActionListenerImplementation(final IComponentLink<T> link, final Object[] mapping,
				final ButtonGroup group) {
			this.link = link;
			this.mapping = mapping;
			this.group = group;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			for (int i = 0; i < mapping.length; i += 2) {
				if (Objects.equals(mapping[i + 1], e.getSource())) {
					link.setValueFromComponent(group, (T) mapping[i]);
				}
			}
		}
	}

	public static <T> IComponentBinding<T> group(final ButtonGroup group, final Object... mapping) {
		return new IComponentBinding<>() {
			ActionListenerImplementation<T> actionListener;

			@Override
			public void addComponentValueChangeListener(final IComponentLink<T> link) {
				actionListener = new ActionListenerImplementation<>(link, mapping, group);
				final var elements = group.getElements();
				while (elements.hasMoreElements()) {
					elements.nextElement().addActionListener(actionListener);
				}
			}

			@Override
			public void setComponentValue(final AbstractProperty source, final T value) {
				for (int i = 0; i < mapping.length; i += 2) {
					if (Objects.equals(mapping[i], value)) {
						group.setSelected(((AbstractButton) mapping[i + 1]).getModel(), true);
					}
				}

			}

			@Override
			public void removeComponentValueChangeListener() {
				final var elements = group.getElements();
				while (elements.hasMoreElements()) {
					elements.nextElement().removeActionListener(actionListener);
				}
			}

			@Override
			public String toString() {
				return "Binding to group of buttons " + group;
			}
		};
	}

}
