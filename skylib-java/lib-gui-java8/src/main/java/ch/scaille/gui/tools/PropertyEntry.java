package ch.scaille.gui.tools;

import java.util.function.Function;

import ch.scaille.gui.mvc.BindingChain.EndOfChain;
import ch.scaille.gui.mvc.properties.AbstractTypedProperty;

/*
 */
public class PropertyEntry {

	protected final AbstractTypedProperty<?> property;
	private final Function<AbstractTypedProperty<?>, EndOfChain<?>> endOfChain;
	private final Class<?> endOfChainType;
	private final boolean readOnly;
	private final String label;
	private final String tooltip;

	protected PropertyEntry(final AbstractTypedProperty<?> property,
			Function<AbstractTypedProperty<?>, EndOfChain<?>> endOfChainProvider, Class<?> propertyType,
			final boolean readOnly, final String label, final String tooltip) {
		this.property = property;
		this.endOfChain = endOfChainProvider;
		this.endOfChainType = propertyType;
		this.label = label;
		this.tooltip = tooltip;
		this.readOnly = readOnly;
	}

	protected PropertyEntry(final AbstractTypedProperty<?> property, Class<?> propertyType, final boolean readOnly,
			final String label, final String tooltip) {
		this(property, AbstractTypedProperty::createBindingChain, propertyType, readOnly, label, tooltip);
	}

	public AbstractTypedProperty<?> getProperty() {
		return property;
	}

	public <V> EndOfChain<V> getChain(Class<V> expectedType) {
		if (!expectedType.equals(endOfChainType)) {
			throw new IllegalArgumentException(
					"Expected " + expectedType + ", but property type is " + getEndOfChainType());
		}
		return ((EndOfChain<V>) endOfChain.apply(property));
	}

	public void loadFromCurrentObject(final Object caller) {
		property.load(caller);
	}

	public void saveInCurrentObject() {
		property.save();
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public String getLabel() {
		return label;
	}

	public String getTooltip() {
		return tooltip;
	}

	public Class<?> getEndOfChainType() {
		return endOfChainType;
	}

	public static String descriptionKey(final String name) {
		return name + ".description";
	}

	public static String tooltipKey(final String name) {
		return name + ".tooltip";
	}
}
