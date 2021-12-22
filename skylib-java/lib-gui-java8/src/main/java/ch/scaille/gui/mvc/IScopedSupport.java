package ch.scaille.gui.mvc;

import java.beans.PropertyChangeListener;

import ch.scaille.gui.mvc.properties.AbstractProperty;

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
	 * Attaches all the properties to the bindings.
	 *
	 * Should be called after all the components are bound to the properties
	 */
	void attachAll();

	/**
	 * Detaches all the properties of this scope.
	 */
	void detachAll();

	void transmitAllToComponentOnly();

	void enableAllTransmit();

	void disposeBindings();

	ControllerPropertyChangeSupport getMain();

	IPropertyEventListener detachWhenPropLoading();

}
