package ch.skymarshall.gui.mvc;

import java.beans.PropertyChangeListener;

import ch.skymarshall.gui.mvc.properties.AbstractProperty;

public interface IScopedSupport {

	void register(AbstractProperty abstractProperty);

	void addPropertyChangeListener(String name, PropertyChangeListener propertyChangeListener);

	void attachAll();

	void disposeBindings();

	ControllerPropertyChangeSupport getMain();

}
