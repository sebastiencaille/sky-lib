package ch.scaille.javabeans;

import java.beans.PropertyChangeListener;

import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.IPropertyEventListener;

public class DummyPropertiesGroup implements IPropertiesGroup {

	@Override
	public void register(AbstractProperty abstractProperty) {
		// noop
	}

	@Override
	public void addPropertyChangeListener(String name, PropertyChangeListener propertyChangeListener) {
		// noop
	}

	@Override
	public void attachAll() {
		// noop
	}

	@Override
	public void detachAll() {
		// noop

	}

	@Override
	public void transmitAllToComponentOnly() {
		// noop

	}

	@Override
	public void enableAllTransmit() {
		// noop

	}

	@Override
	public void disposeBindings() {
		// noop

	}

	@Override
	public PropertyChangeSupportController getChangeSupport() {
		// noop
		return null;
	}

	@Override
	public IPropertyEventListener detachWhenPropLoading() {
		// noop
		return null;
	}

}
