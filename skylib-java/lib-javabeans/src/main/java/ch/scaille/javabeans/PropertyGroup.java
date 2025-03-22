package ch.scaille.javabeans;

import javax.swing.event.EventListenerList;

import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.IPropertyEventListener;

/**
 * Allows trigger {@link IPropertyEventListener} when the value of a property
 * in this group is changed
 * 
 * @author Sebastien Caille
 * 
 */
public class PropertyGroup {

	private final EventListenerList actions = new EventListenerList();

	private int callCount = 0;

	private class Impl implements IPropertyEventListener {
		@Override
		public void propertyModified(final Object caller, final PropertyEvent event) {
			switch (event.kind()) {
			case BEFORE:
				if (callCount > 0) {
					return;
				}
				callCount++;
				break;

			case AFTER:
				callCount--;
				if (callCount != 0) {
					return;
				}
				break;
			default:
				break;
			}
			for (final var actionListener : actions.getListeners(IPropertyEventListener.class)) {
				actionListener.propertyModified(caller, event);
			}
		}
	}

	private final Impl impl = new Impl();

	public void addProperty(final AbstractProperty prop) {
		prop.addListener(impl);
	}

	public void addListener(final IPropertyEventListener action) {
		actions.add(IPropertyEventListener.class, action);
	}

}
