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
package org.skymarshall.hmi.mvc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.skymarshall.hmi.mvc.properties.AbstractProperty;

/**
 * Allows enabling some properties on a per-object basis.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 *            type of the object according to which the properties are enabled
 */
public class BindingSelector<T> implements PropertyChangeListener {

	private final Map<T, List<IBindingController>> objectControllers = new WeakHashMap<>();

	public BindingSelector(final AbstractProperty property) {
		property.addListener(this);
	}

	public void add(final T object, final IBindingController... controllers) {
		List<IBindingController> ctrls = objectControllers.get(object);
		if (ctrls == null) {
			ctrls = new ArrayList<>();
			objectControllers.put(object, ctrls);
		}
		ctrls.addAll(Arrays.asList(controllers));
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {

		final Object oldValue = evt.getOldValue();
		if (oldValue != null) {
			final List<IBindingController> toDetach = objectControllers.get(oldValue);
			for (final IBindingController controller : toDetach) {
				controller.detach();
			}
		}

		final Object newValue = evt.getNewValue();
		if (newValue != null) {
			final List<IBindingController> toAttach = objectControllers.get(newValue);
			if (toAttach != null) {
				for (final IBindingController controller : toAttach) {
					controller.attach();
				}
			}
		}

	}

}
