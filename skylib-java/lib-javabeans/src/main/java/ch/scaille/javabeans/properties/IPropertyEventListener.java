package ch.scaille.javabeans.properties;

import java.util.EventListener;

import ch.scaille.javabeans.PropertyEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Listener on some extra property events.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 */
@NullMarked
public interface IPropertyEventListener extends EventListener {

	/**
	 * Called before the value is set
	 * 
	 * @param caller   the caller
	 * @param event the modified property
	 */
	void propertyModified(Object caller, PropertyEvent event);

}
