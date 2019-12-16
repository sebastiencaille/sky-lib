package ch.skymarshall.gui.mvc;

import java.beans.PropertyChangeListener;

import ch.skymarshall.gui.mvc.properties.AbstractProperty;

/**
 * Scopped support allows acting on a subset of the initial change support
 *
 * @author scaille
 *
 */
public interface IScopedSupport {

	void register(AbstractProperty abstractProperty);

	void addPropertyChangeListener(String name, PropertyChangeListener propertyChangeListener);

	/**
	 * Detaches all the properties of this scope.
	 *
	 * Use this method to avoid unexpected triggering of listeners when your model
	 * is in an inconsistent state (ie when you are loading new objects).
	 */
	void detachAll();

	void attachAll();

	void disposeBindings();

	ControllerPropertyChangeSupport getMain();

}
