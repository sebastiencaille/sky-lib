package ch.scaille.gui.tools;

import java.util.function.Function;

import ch.scaille.javabeans.BindingChain.EndOfChain;
import ch.scaille.javabeans.properties.AbstractTypedProperty;

/*
 * A property based entry of the editor.
 * 
 * @param <T> the owner type
 * @param <V> the end of chain type
 */
public class PropertyEntry<T, V> implements IPropertyEntry<T> {

	protected final AbstractTypedProperty<V> property;
	private final Function<AbstractTypedProperty<V>, EndOfChain<V>> endOfChain;
	private final Class<?> propertyType;
	private final boolean readOnly;
	private final String label;
	private final String tooltip;

	protected PropertyEntry(Class<V> propertyType,
			final AbstractTypedProperty<V> property, Function<AbstractTypedProperty<V>, EndOfChain<V>> endOfChainProvider,
			final boolean readOnly, final String label, final String tooltip) {
		this.property = property;
		this.endOfChain = endOfChainProvider;
		this.propertyType = propertyType;
		this.label = label;
		this.tooltip = tooltip;
		this.readOnly = readOnly;
	}

	protected PropertyEntry(final AbstractTypedProperty<V> property, Class<V> propertyType, final boolean readOnly,
			final String label, final String tooltip) {
		this(propertyType, property, AbstractTypedProperty::createBindingChain, readOnly, label, tooltip);
	}

	@Override
	public <W> PropertyEntry<T, W> as(Class<W> expectedType) {
		if (!expectedType.equals(propertyType)) {
			throw new IllegalArgumentException(
					"Expected " + expectedType + ", but property type is " + getPropertyType());
		}
		return (PropertyEntry<T, W>)this;
	}
	
	@Override
	public AbstractTypedProperty<V> getProperty() {
		return property;
	}

	/**
	 * Allows to get the typed EndOfChain of the parameterized entry
	 * @param <V> the expected type
	 * @param expectedType the expected type
	 * @return the typed EndOfChain
	 */
	public EndOfChain<V> getChain() {
		return endOfChain.apply(property);
	}

	@Override
	public void loadFromCurrentObject(final Object caller) {
		property.load(caller);
	}

	@Override
	public void saveInCurrentObject() {
		property.save();
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getTooltip() {
		return tooltip;
	}

	@Override
	public Class<?> getPropertyType() {
		return propertyType;
	}

	public static String descriptionKey(final String name) {
		return name + ".description";
	}

	public static String tooltipKey(final String name) {
		return name + ".tooltip";
	}
}
