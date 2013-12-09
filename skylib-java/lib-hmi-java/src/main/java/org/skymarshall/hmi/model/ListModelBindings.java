/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.model;

import java.util.Collection;

import org.skymarshall.hmi.model.views.IListView;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

public class ListModelBindings {

    public static <T> IComponentBinding<IListView<T>> view(final ListModel<T> model) {
        return new IComponentBinding<IListView<T>>() {
            @Override
            public void setComponentValue(final AbstractProperty source, final IListView<T> view) {
                model.setView(view);
            }

            @Override
            public void addComponentValueChangeListener(final IComponentLink<IListView<T>> converter) {
                // nope
            }

            @Override
            public Object getComponent() {
                return model;
            }
        };
    }

    public static <T> IComponentBinding<Collection<T>> values(final ListModel<T> model) {
        return new IComponentBinding<Collection<T>>() {
            @Override
            public void addComponentValueChangeListener(final IComponentLink<Collection<T>> converter) {
                // nope
            }

            @Override
            public void setComponentValue(final AbstractProperty source, final Collection<T> values) {
                model.setValues(values);

            }

            @Override
            public Object getComponent() {
                return model;
            }
        };
    }
}
