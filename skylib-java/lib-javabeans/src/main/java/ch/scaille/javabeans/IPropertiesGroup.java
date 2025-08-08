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
public interface IPropertiesGroup extends IPropertyController {

	void register(AbstractProperty abstractProperty);

	void addPropertyChangeListener(String name, PropertyChangeListener propertyChangeListener);

	void forAllProperties(Consumer<AbstractProperty> applier);

	PropertyChangeSupportController getChangeSupport();

	IPropertyEventListener detachWhenPropLoading();

}
