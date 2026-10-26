package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.ErrorNotifier;


public interface IPropertiesOwner {

	IPropertiesGroup getPropertySupport();

	ErrorNotifier getErrorNotifier();

}
