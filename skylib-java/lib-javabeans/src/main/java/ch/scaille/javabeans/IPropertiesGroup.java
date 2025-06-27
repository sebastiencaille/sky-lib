package ch.scaille.javabeans;

import java.beans.PropertyChangeListener;
import java.util.function.Consumer;

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

	void forAllProperties(Consumer<AbstractProperty> applier);

	/**
	 * Detaches all the properties of this scope.
	 */
	void bufferizeChanges();

	/**
	 * Flushes all the properties to the bindings and enable the transmission.
	 * <p>
	 * Should be called after all the components are bound to the properties
	 * </p>
	 */
	void flushChanges();
	
	void transmitChangesOnlyToComponent();

	void transmitChangesBothWays();

	void disposeBindings();

	PropertyChangeSupportController getChangeSupport();

	IPropertyEventListener detachWhenPropLoading();

}
