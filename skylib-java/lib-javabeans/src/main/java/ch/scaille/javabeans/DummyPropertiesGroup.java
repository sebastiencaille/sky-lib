package ch.scaille.javabeans;

import java.beans.PropertyChangeListener;
import java.util.function.Consumer;

import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.IPropertyEventListener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DummyPropertiesGroup implements IPropertiesGroup, IPropertyEventListener {

	private final PropertyChangeSupportController propertyChangeSupportController = new PropertyChangeSupportController(this);

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
		return propertyChangeSupportController;
	}

	@Override
	public IPropertyEventListener detachWhenPropLoading() {
		// noop
		return this;
	}
	
	@Override
	public void flushChanges() {
		// noop
	}

	@Override
	public void propertyModified(Object caller, PropertyEvent event) {
		// noop
	}
}
