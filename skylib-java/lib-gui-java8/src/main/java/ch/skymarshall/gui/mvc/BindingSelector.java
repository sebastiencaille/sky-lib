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
package ch.skymarshall.gui.mvc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import ch.skymarshall.gui.mvc.properties.AbstractProperty;

/**
 * Allows enabling some properties on a per-object basis.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> type of the object according to which the properties are enabled
 */
public class BindingSelector<T> implements PropertyChangeListener {

	private final Map<T, List<IBindingController>> objectControllers = new WeakHashMap<>();

	public BindingSelector(final AbstractProperty property) {
		property.addListener(this);
	}

	public void add(final T object, final IBindingController... controllers) {
		final List<IBindingController> ctrls = objectControllers.computeIfAbsent(object, k -> new ArrayList<>());
		ctrls.addAll(Arrays.asList(controllers));
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {

		final Object oldValue = evt.getOldValue();
		if (oldValue != null) {
			final List<IBindingController> oldControllers = objectControllers.get(oldValue);
			if (oldControllers != null) {
				oldControllers.forEach(c -> c.getVeto().detach());
			}
		}

		final Object newValue = evt.getNewValue();
		if (newValue != null) {
			final List<IBindingController> newController = objectControllers.get(newValue);
			if (newController != null) {
				newController.forEach(c -> c.getVeto().attach());
				newController.forEach(IBindingController::forceViewUpdate);
			}
		}

	}

}
