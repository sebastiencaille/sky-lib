package ch.scaille.javabeans.properties;

import java.util.EventListener;

import ch.scaille.javabeans.PropertyEvent;

/**
 * Listener on some extra property events.
 * <p>
 * 

 *  */
public interface IPropertyEventListener extends EventListener {

	/**
	 * Called before the value is set
	 * 
	 * @param caller   the caller
	 * @param event the modified property
	 */
	void propertyModified(Object caller, PropertyEvent event);

}
