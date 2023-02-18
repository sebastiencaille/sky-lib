package ch.scaille.gui.tools;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import ch.scaille.gui.mvc.BindingChain.EndOfChain;
import ch.scaille.gui.mvc.IScopedSupport;
import ch.scaille.gui.mvc.persisters.ObjectProviderPersister.IObjectProvider;
import ch.scaille.gui.mvc.properties.AbstractTypedProperty;

/**
 * 
 * @author scaille
 *
 * @param <T> type of the persisted object
 */
public class SimpleEditorModel<T> implements IGenericEditorModel<T> {

	public static <E> PropertyEntry entry(final AbstractTypedProperty<?> property,
			final Function<AbstractTypedProperty<?>, EndOfChain<?>> endOfChainProvider, Class<E> endOfChainType,
			final boolean readOnly, final String label, final String tooltip) {
		return new PropertyEntry(property, endOfChainProvider, endOfChainType, readOnly, label, tooltip);
	}

	public static <E> PropertyEntry entry(final AbstractTypedProperty<?> property,
			final Function<AbstractTypedProperty<?>, EndOfChain<?>> endOfChainProvider, Class<E> endOfChainType,
			final boolean readOnly, final UnaryOperator<String> textProvider) {
		return new PropertyEntry(property, endOfChainProvider, endOfChainType, readOnly,
				textProvider.apply(PropertyEntry.descriptionKey(property.getName())),
				textProvider.apply(PropertyEntry.tooltipKey(property.getName())));
	}

	private final BiFunction<IScopedSupport, IObjectProvider<T>, List<PropertyEntry>> builder;

	public SimpleEditorModel(final BiFunction<IScopedSupport, IObjectProvider<T>, List<PropertyEntry>> builder) {
		this.builder = builder;
	}

	/**
	 * Creates the properties by introspecting the displayed object
	 *
	 * @param propertySupport
	 *
	 * @return
	 */
	@Override
	public List<PropertyEntry> createProperties(final IScopedSupport propertySupport, IObjectProvider<T> object) {
		return builder.apply(propertySupport, object);
	}

}
