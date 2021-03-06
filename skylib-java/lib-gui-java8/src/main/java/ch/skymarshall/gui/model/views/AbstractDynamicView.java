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
package ch.skymarshall.gui.model.views;

import java.util.function.Consumer;

import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.factories.ComponentBindings;

public abstract class AbstractDynamicView<T> {

	private IListViewOwner<T> viewOwner;

	public void attach(final IListViewOwner<T> aViewOwner) {
		this.viewOwner = aViewOwner;
	}

	public void detach(final IListViewOwner<T> aViewOwner) {
		if (viewOwner == aViewOwner) {
			this.viewOwner = null;
		}
	}

	/**
	 * Returns a component binding that calls c with the new value and refreshes the
	 * view
	 *
	 * @param c
	 * @return
	 */
	public <U> IComponentBinding<U> refreshWhenUpdated(final Consumer<U> c) {
		return ComponentBindings.wo((s, v) -> {
			c.accept(v);
			if (viewOwner != null) {
				viewOwner.viewUpdated();
			}
		});
	}

}
