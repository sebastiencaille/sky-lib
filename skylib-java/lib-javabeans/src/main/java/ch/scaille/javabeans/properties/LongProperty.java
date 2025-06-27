package ch.scaille.javabeans.properties;

import java.util.function.Consumer;

import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.chain.BindingChain;

/**
 * Property containing a long value.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public class LongProperty extends AbstractTypedProperty<Long> {

	private long value;
	private final long defaultValue;

	public LongProperty(final String name, final IPropertiesOwner model, final long defaultValue) {
		super(name, model);
		this.defaultValue = defaultValue;
	}

	public LongProperty(final String name, final IPropertiesOwner model) {
		this(name, model, 0);
	}

	public LongProperty(final String name, final IPropertiesGroup propertySupport, final long defaultValue) {
		super(name, propertySupport);
		this.defaultValue = defaultValue;
	}

	public LongProperty(final String name, final IPropertiesGroup propertySupport) {
		this(name, propertySupport, 0);
	}

	@Override
	public IChainBuilderFactory<Long> createBindingChain() {
		return new BindingChain(this, errorNotifier).bindProperty(this::setObjectValueFromComponent);
	}

	@SafeVarargs
	@Override
	public final LongProperty configureTyped(final Consumer<AbstractTypedProperty<Long>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
	}

	public long getValue() {
		return value;
	}

	public void setValue(final Object caller, final long newValue) {
		setObjectValue(caller, newValue);
	}

	@Override
	protected Long replaceValue(final Long newValue) {
		if (newValue == null) {
			throw new IllegalArgumentException("Null value is not allowed");
		}
		final var oldValue = value;
		value = newValue;
		return oldValue;
	}

	@Override
	public Long getObjectValue() {
		return getValue();
	}

	@Override
	public void flush() {
		super.flush();
		propertySupport.getChangeSupport().firePropertyChange(getName(), this, null, value);
	}

	@Override
	public void reset(final Object caller) {
		setObjectValue(this, defaultValue);
	}

}
