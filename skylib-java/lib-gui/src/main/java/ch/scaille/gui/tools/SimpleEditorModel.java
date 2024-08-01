package ch.scaille.gui.tools;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import ch.scaille.javabeans.BindingChain.EndOfChain;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.persisters.IPersisterFactory.IObjectProvider;
import ch.scaille.javabeans.properties.AbstractTypedProperty;
import ch.scaille.javabeans.properties.ErrorSet;

/**
 * A simple editor, with an arbitrary content
 * 
 * @param <T> type of the persisted object
 * @author scaille
 */
public class SimpleEditorModel<T> implements IGenericEditorModel<T> {

	/**
	 * Creates an entry of the model
	 */
    public static <T, E> IPropertyEntry<T> entry(Class<E> propertyType,
                                          final AbstractTypedProperty<E> property, final Function<AbstractTypedProperty<E>, EndOfChain<E>> endOfChainProvider,
                                          final boolean readOnly, final String label, final String tooltip) {
        return new PropertyEntry<>(propertyType, property, endOfChainProvider, readOnly, label, tooltip);
    }

	/**
	 * Creates an entry of the model
	 */
    public static <T, E> IPropertyEntry<T> entry(Class<E> propertyType,
                                          final AbstractTypedProperty<E> property, final Function<AbstractTypedProperty<E>, EndOfChain<E>> endOfChainProvider,
                                          final boolean readOnly, final UnaryOperator<String> textProvider) {
        return new PropertyEntry<>(propertyType, property, endOfChainProvider, readOnly,
                textProvider.apply(PropertyEntry.descriptionKey(property.getName())),
                textProvider.apply(PropertyEntry.tooltipKey(property.getName())));
    }

    private final IPropertiesGroup support;
    private final ErrorSet errorSet;
    private final BiFunction<IPropertiesGroup, IObjectProvider<T>, List<IPropertyEntry<T>>> builder;

    public SimpleEditorModel(final BiFunction<IPropertiesGroup, IObjectProvider<T>, List<IPropertyEntry<T>>> builder) {
        this.support = PropertyChangeSupportController.mainGroup(this);
        this.errorSet = new ErrorSet("Error", support);
        this.builder = builder;
    }

    /**
     * Creates the properties by introspecting the displayed object
     *
     * @param object
     * @return
     */
    @Override
    public List<IPropertyEntry<T>> createProperties(IObjectProvider<T> object) {
        return builder.apply(support, object);
    }

    @Override
    public IPropertiesGroup getPropertySupport() {
        return support;
    }

    @Override
    public ErrorSet getErrorProperty() {
        return errorSet;
    }
}
