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

import java.awt.Component;

import javax.swing.JTabbedPane;

import ch.skymarshall.gui.mvc.ComponentBindingAdapter;
import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.gui.swing.factories.SwingBindings;

/**
 * Select the tab of a tabbed pane according to the property's value.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class JTabbedPaneSelectionBinding<T> extends ComponentBindingAdapter<T> {

	private final JTabbedPane pane;
	private final Class<T> clazz;

	public JTabbedPaneSelectionBinding(final JTabbedPane component, final Class<T> clazz) {
		this.pane = component;
		this.clazz = clazz;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<T> converter) {
		pane.addChangeListener(e -> {
			final int index = pane.getSelectedIndex();

			if (index >= 0) {

				final Object tabClientProperty = pane.getClientProperty(pane.getComponentAt(index));
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
	public void setComponentValue(final AbstractProperty source, final T value) {
		for (int i = 0; i < pane.getComponentCount(); i++) {
			if (pane.getClientProperty(pane.getComponentAt(i)) == value) {
				pane.setSelectedIndex(i);
				return;
			}
		}
	}

	@Override
	public String toString() {
		return "Selection of " + SwingBindings.nameOf(pane);
	}

	public static void setValueForTab(final JTabbedPane pane, final Component tabPanel,
			final Object tabClientProperty) {
		pane.putClientProperty(tabPanel, tabClientProperty);
	}

	public static Object getValueForTab(final JTabbedPane pane, final Component tabPanel) {
		return pane.getClientProperty(tabPanel);
	}

}
