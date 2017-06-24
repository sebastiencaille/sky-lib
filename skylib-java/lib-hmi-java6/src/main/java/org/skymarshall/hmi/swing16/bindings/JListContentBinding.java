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
package org.skymarshall.hmi.swing16.bindings;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.skymarshall.hmi.mvc.properties.AbstractProperty;

public class JListContentBinding<T> extends AbstractComponentBinding<List<T>> {

    private final JList<T> list;

    public JListContentBinding(final JList<T> component) {
        super(component);
        this.list = component;
    }

    @Override
    public void setComponentValue(final AbstractProperty source, final List<T> value) {
        final DefaultListModel<T> newModel = new DefaultListModel<T>();
        for (final T obj : value) {
            newModel.addElement(obj);
        }
        list.setModel(newModel);
    }
}
