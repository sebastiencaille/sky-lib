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
package ch.skymarshall.gui.model;

import java.util.Collection;

import ch.skymarshall.gui.model.views.IListView;
import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.factories.ComponentBindings;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;

public interface ListModelBindings {

	public static <T> IComponentBinding<IListView<T>> view(final ListModel<T> model) {
		return ComponentBindings.<ListModel<T>, IListView<T>>wo(model, (c, p, t) -> c.setView(t));
	}

	public static <T> IComponentBinding<Collection<T>> values(final ListModel<T> model) {
		return new IComponentBinding<Collection<T>>() {

			private IListModelListener<T> listener;

			@Override
			public void addComponentValueChangeListener(final IComponentLink<Collection<T>> link) {
				listener = IListModelListener.editionStopped(e -> link.setValueFromComponent(model, model.values()));
				model.addListener(listener);
			}

			@Override
			public void setComponentValue(final AbstractProperty source, final Collection<T> value) {
				model.setValues(value);
			}

			@Override
			public void removeComponentValueChangeListener() {
				model.removeListener(listener);
			}

		};
	}
}
