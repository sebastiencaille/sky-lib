package ch.scaille.javabeans.properties;

import java.util.Set;
import java.util.function.Consumer;

import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.IPropertiesGroup;

public class SetProperty<T> extends ObjectProperty<Set<T>> {

	public SetProperty(final String name, final IPropertiesOwner model) {
		super(name, model);
	}

	public SetProperty(final String name, final IPropertiesGroup propertySupport) {
		super(name, propertySupport);
	}

	@SafeVarargs
	@Override
	public final SetProperty<T> configureTyped(final Consumer<AbstractTypedProperty<Set<T>>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
	}
}
