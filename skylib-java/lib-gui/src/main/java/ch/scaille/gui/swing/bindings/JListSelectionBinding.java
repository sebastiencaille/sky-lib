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

import javax.swing.JList;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.gui.swing.factories.SwingBindings;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.properties.AbstractProperty;

public class JListSelectionBinding<T> extends ComponentBindingAdapter<T> {

	private final JList<T> list;
	private IComponentLink<T> link;

	public JListSelectionBinding(final JList<T> component) {
		this.list = component;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<T> fromLink) {
		this.link = fromLink;
		list.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				fromLink.setValueFromComponent(list, list.getSelectedValue());
			}
		});
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final T value) {
		if (!source.isModifiedBy(list)) {
			list.setSelectedValue(value, true);
			if (list.getSelectedValue() == null) {
				link.setValueFromComponent(list, null);
			}
		}
	}

	@Override
	public String toString() {
		return "Selection of " + SwingBindings.nameOf(list);
	}
}
