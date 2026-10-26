package ch.scaille.javabeans.properties;

import java.util.function.Consumer;

import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.chain.BindingChain;
import lombok.Getter;

/**
 * Property containing an int value.
 */
public class IntProperty extends AbstractTypedProperty<Integer, IntProperty> {

	@Getter
	private int value;
	private final int defaultValue;

	public IntProperty(final String name, final IPropertiesOwner model, final int defaultValue) {
		super(name, model);
		this.defaultValue = defaultValue;
	}

	public IntProperty(final String name, final IPropertiesOwner model) {
		this(name, model, 0);
	}

	public IntProperty(final String name, final IPropertiesGroup propertySupport, final int defaultValue) {
		super(name, propertySupport);
		this.defaultValue = defaultValue;
	}

	public IntProperty(final String name, final IPropertiesGroup propertySupport) {
		this(name, propertySupport, 0);
	}

	@Override
	public IChainBuilderFactory<Integer> createBindingChain() {
		return new BindingChain(this, errorNotifier).bindProperty(this::setObjectValueFromComponent);
	}

	@SafeVarargs
	@Override
	public final IntProperty configureTyped(final Consumer<? super IntProperty>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
	}

	public void setValue(final Object caller, final int newValue) {
		setObjectValue(caller, newValue);
	}

	@Override
	protected Integer replaceValue(final Integer newValue) {
		final var oldValue = value;
		value = newValue;
		return oldValue;
	}

	@Override
	public Integer getObjectValue() {
		return getValue();
	}

	@Override
	public void reset(final Object caller) {
		setObjectValue(this, defaultValue);
	}

}
