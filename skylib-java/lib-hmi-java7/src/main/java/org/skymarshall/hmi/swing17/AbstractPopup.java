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
package org.skymarshall.hmi.swing17;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import org.skymarshall.hmi.mvc.properties.ObjectProperty;

public abstract class AbstractPopup<T> extends MouseAdapter {

    private final JPopupMenu          componentPopupMenu = new JPopupMenu();
    protected final ObjectProperty<T> lastSelected;

    protected abstract void buildPopup(JPopupMenu popupMenu, T selected);

    public AbstractPopup(final ObjectProperty<T> lastSelected) {
        this.lastSelected = lastSelected;
    }

    protected T getValueForPopup(final Point p) {
        return lastSelected.getValue();
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            final T selected = getValueForPopup(e.getPoint());
            buildPopup(componentPopupMenu, selected);
            componentPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

}
