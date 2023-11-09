package ch.scaille.javabeans.properties;

import java.util.List;
import java.util.function.Consumer;

import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.IPropertiesGroup;

/**
 * Property containing a list of Objects.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> type of the contained objects
 */
public class ListProperty<T> extends ObjectProperty<List<T>> {

	public ListProperty(final String name, final IPropertiesOwner model) {
		super(name, model);
	}

	public ListProperty(final String name, final IPropertiesGroup propertySupport) {
		super(name, propertySupport);
	}

	@SafeVarargs
	@Override
	public final ListProperty<T> configureTyped(final Consumer<AbstractTypedProperty<List<T>>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
	}

}
