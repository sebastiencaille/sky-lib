package ch.scaille.javabeans.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.IPropertiesGroup;

public class MapProperty<T, U> extends ObjectProperty<Map<T, U>> {

	public MapProperty(final String name, final IPropertiesOwner model) {
		super(name, model, new HashMap<>());
	}

	public MapProperty(final String name, final IPropertiesGroup propertySupport) {
		super(name, propertySupport, new HashMap<>());
	}

	@SafeVarargs
	@Override
	public final MapProperty<T, U> configureTyped(
			final Consumer<AbstractTypedProperty<Map<T, U>>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
	}
}
