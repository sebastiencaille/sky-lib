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
package ch.scaille.gui.swing;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import ch.scaille.javabeans.properties.ObjectProperty;

public abstract class AbstractPopup<T> extends MouseAdapter {

	private final JPopupMenu componentPopupMenu = new JPopupMenu();
	protected final ObjectProperty<T> lastSelected;

	protected abstract void buildPopup(JPopupMenu popupMenu, T selected);

	protected AbstractPopup(final ObjectProperty<T> lastSelected) {
		this.lastSelected = lastSelected;
	}

	/**
	 * 
	 * @param p location of the popup
	 * @return
	 */
	protected T getValueForPopup(final Point p) {
		return lastSelected.getValue();
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (e.isPopupTrigger()) {
			final var selected = getValueForPopup(e.getPoint());
			buildPopup(componentPopupMenu, selected);
			componentPopupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

}
