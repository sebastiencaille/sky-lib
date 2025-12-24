package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IPropertiesOwner {

	IPropertiesGroup getPropertySupport();

	ErrorNotifier getErrorNotifier();

}
