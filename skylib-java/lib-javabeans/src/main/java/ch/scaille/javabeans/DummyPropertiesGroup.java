package ch.scaille.javabeans;

import java.beans.PropertyChangeListener;
import java.util.function.Consumer;

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
	public void forAllProperties(Consumer<AbstractProperty> applier) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void flushChanges() {
		// noop
	}

	@Override
	public void bufferizeChanges() {
		// noop

	}

	@Override
	public void transmitChangesOnlyToComponent() {
		// noop

	}

	@Override
	public void transmitChangesBothWays() {
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
