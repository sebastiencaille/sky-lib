package ch.scaille.javabeans.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;


public class MapProperty<T, U> extends ExtendableObjectProperty<Map<T, U>, MapProperty<T, U>> {

	public MapProperty(final String name, final IPropertiesOwner model) {
		super(name, model, new HashMap<>());
	}

	public MapProperty(final String name, final IPropertiesGroup propertySupport) {
		super(name, propertySupport, new HashMap<>());
	}

	@SafeVarargs
	@Override
	public final MapProperty<T, U> configureTyped(
			final Consumer<? super MapProperty<T, U>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
	}

}
