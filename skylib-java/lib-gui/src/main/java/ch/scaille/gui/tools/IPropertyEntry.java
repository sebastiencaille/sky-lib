package ch.scaille.gui.tools;

import ch.scaille.javabeans.properties.AbstractProperty;

public interface IPropertyEntry<T> {

	String getLabel();

	String getTooltip();

	boolean isReadOnly();

	AbstractProperty getProperty();

	Class<?> getPropertyType();

	<U> PropertyEntry<T, U> as(Class<U> clazz);

	void loadFromCurrentObject(final Object caller);

	void saveInCurrentObject();

}
