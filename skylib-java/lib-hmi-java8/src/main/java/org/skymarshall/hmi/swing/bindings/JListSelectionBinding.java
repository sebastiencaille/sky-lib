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

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

public class JListSelectionBinding<T> extends AbstractComponentBinding<T> {

    private final JList<T>    list;
    private final Class<T>    clazz;
    private IComponentLink<T> link;

    public JListSelectionBinding(final JList<T> component, final Class<T> clazz) {
        super(component);
        this.list = component;
        this.clazz = clazz;
    }

    @Override
    public void addComponentValueChangeListener(final IComponentLink<T> fromLink) {
        this.link = fromLink;
        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    fromLink.setValueFromComponent(list, clazz.cast(list.getSelectedValue()));
                }
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
}
