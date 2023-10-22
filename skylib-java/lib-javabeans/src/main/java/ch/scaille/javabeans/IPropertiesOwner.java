package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;

public interface IPropertiesOwner {

	IPropertiesGroup getPropertySupport();

	ErrorNotifier getErrorNotifier();

}
