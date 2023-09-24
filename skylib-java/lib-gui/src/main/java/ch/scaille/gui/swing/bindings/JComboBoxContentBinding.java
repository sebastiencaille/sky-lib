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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.gui.mvc.properties.AbstractProperty;
import ch.scaille.gui.swing.factories.SwingBindings;

public class JComboBoxContentBinding<T, U extends Collection<T>> extends ComponentBindingAdapter<U> {

	private final JComboBox<T> box;

	public JComboBoxContentBinding(final JComboBox<T> component) {
		this.box = component;
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final U value) {
		final var newModel = new DefaultComboBoxModel<T>();
		value.forEach(newModel::addElement);
		box.setModel(newModel);
	}

	@Override
	public String toString() {
		return "Value of " + SwingBindings.nameOf(box);
	}
}
