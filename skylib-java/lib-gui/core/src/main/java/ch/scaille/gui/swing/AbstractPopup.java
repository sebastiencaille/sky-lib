package ch.scaille.gui.swing;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import ch.scaille.javabeans.properties.ObjectProperty;
import org.jspecify.annotations.Nullable;

public abstract class AbstractPopup<T> extends MouseAdapter {

	private final JPopupMenu componentPopupMenu = new JPopupMenu();

	@Nullable
	protected final ObjectProperty<@Nullable T> lastSelected;

	protected abstract void buildPopup(JPopupMenu popupMenu, T selected);

	protected AbstractPopup(@Nullable final ObjectProperty<@Nullable T> lastSelected) {
		this.lastSelected = lastSelected;
	}

	/**
	 * 
	 * @param p location of the popup
	 */
	@Nullable
	protected T getValueForPopup(final Point p) {
		if (lastSelected == null) {
			return null;
		}
		return lastSelected.getValue();
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (e.isPopupTrigger()) {
			final var selected = getValueForPopup(e.getPoint());
			if (selected != null) {
				buildPopup(componentPopupMenu, selected);
				componentPopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

}
