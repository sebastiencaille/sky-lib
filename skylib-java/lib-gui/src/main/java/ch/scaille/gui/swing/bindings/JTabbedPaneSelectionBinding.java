package ch.scaille.gui.swing.bindings;

import java.awt.Component;

import javax.swing.JTabbedPane;

import ch.scaille.gui.swing.SwingExt;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import org.jspecify.annotations.NullMarked;

/**
 * Select the tab of a tabbed pane according to the property's value.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
@NullMarked
public class JTabbedPaneSelectionBinding<T> implements IComponentBinding<T> {

	private final JTabbedPane pane;
	private final Class<T> clazz;

	public JTabbedPaneSelectionBinding(final JTabbedPane component, final Class<T> clazz) {
		this.pane = component;
		this.clazz = clazz;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<T> converter) {
		pane.addChangeListener(e -> {
			final var index = pane.getSelectedIndex();

			if (index >= 0) {

				final var tabClientProperty = pane.getClientProperty(pane.getComponentAt(index));
				if (tabClientProperty == null) {
					return;
				}
				if (!clazz.isInstance(tabClientProperty)) {
					throw new IllegalStateException("Property of tab " + index + " must be a " + clazz.getName());
				}
				converter.setValueFromComponent(pane, clazz.cast(tabClientProperty));
			}

		});
	}

	@Override
	public void setComponentValue(final IComponentChangeSource source, final T value) {
		for (int i = 0; i < pane.getComponentCount(); i++) {
			if (pane.getClientProperty(pane.getComponentAt(i)) == value) {
				pane.setSelectedIndex(i);
				return;
			}
		}
	}

	@Override
	public String toString() {
		return "Selection of " + SwingExt.nameOf(pane);
	}

	public static void setValueForTab(final JTabbedPane pane, final Component tabPanel,
			final Object tabClientProperty) {
		pane.putClientProperty(tabPanel, tabClientProperty);
	}

	public static <T> T getValueForTab(final JTabbedPane pane, final Component tabPanel, Class<T> type) {
		return type.cast(pane.getClientProperty(tabPanel));
	}

}
