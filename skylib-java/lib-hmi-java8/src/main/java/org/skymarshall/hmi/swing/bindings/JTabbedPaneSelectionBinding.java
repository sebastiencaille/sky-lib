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

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

/**
 * Select the tab of a tabbed pane according to the property's value.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class JTabbedPaneSelectionBinding<T> extends DefaultComponentBinding<T> {

	private final JTabbedPane pane;
	private final Class<T> clazz;

	public JTabbedPaneSelectionBinding(final JTabbedPane component, final Class<T> clazz) {
		this.pane = component;
		this.clazz = clazz;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<T> converter) {
		pane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent e) {
				final int index = pane.getSelectedIndex();

				if (index >= 0) {

					final Object clientProperty = pane.getClientProperty(pane.getComponentAt(index));
					if (clientProperty == null) {
						// throw new IllegalStateException("Object for tab " +
						// index + " does not exist");
						return;
					}
					if (!clazz.isInstance(clientProperty)) {
						throw new IllegalStateException("Object for tab " + index + " must be a " + clazz.getName());
					}
					converter.setValueFromComponent(pane, clazz.cast(clientProperty));
				}
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

	public static void setValueForComponent(final JTabbedPane pane, final JComponent component, final Object value) {
		pane.putClientProperty(component, value);
	}

}
