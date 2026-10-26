package ch.scaille.javabeans.properties;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;


public class SetProperty<T> extends ExtendableObjectProperty<Set<T>, SetProperty<T>> {

	public SetProperty(final String name, final IPropertiesOwner model) {
		super(name, model, new HashSet<>());
	}

	public SetProperty(final String name, final IPropertiesGroup propertySupport) {
		super(name, propertySupport, new HashSet<>());
	}

	@SafeVarargs
	@Override
	public final SetProperty<T> configureTyped(final Consumer<? super SetProperty<T>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
	}
}
