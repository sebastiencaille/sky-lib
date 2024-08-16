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
