package ch.scaille.javabeans;

import java.beans.PropertyChangeListener;

import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.IPropertyEventListener;

/**
 * A group of properties allows controlling a group of properties
 *
 * @author scaille
 *
 */
public interface IPropertiesGroup {

	void register(AbstractProperty abstractProperty);

	void addPropertyChangeListener(String name, PropertyChangeListener propertyChangeListener);

	/**
	 * Attaches all the properties to the bindings.
	 * <p>
	 * Should be called after all the components are bound to the properties
	 * </p>
	 */
	void attachAll();

	/**
	 * Detaches all the properties of this scope.
	 */
	void detachAll();

	void transmitAllToComponentOnly();

	void enableAllTransmit();

	void disposeBindings();

	PropertyChangeSupportController getChangeSupport();

	IPropertyEventListener detachWhenPropLoading();

}
