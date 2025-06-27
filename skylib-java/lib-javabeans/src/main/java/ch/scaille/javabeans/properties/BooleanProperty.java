package ch.scaille.javabeans.properties;

import java.util.function.Consumer;

import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.chain.BindingChain;

/**
 * Property containing a boolean value.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public class BooleanProperty extends AbstractTypedProperty<Boolean> {
	private boolean value;
	private final boolean defaultValue;

	public BooleanProperty(final String name, final IPropertiesOwner model, final boolean defaultValue) {
		super(name, model);
		this.defaultValue = defaultValue;
	}

	public BooleanProperty(final String name, final IPropertiesOwner model) {
		this(name, model, false);
	}

	public BooleanProperty(final String name, final IPropertiesGroup propertySupport, final boolean defaultValue) {
		super(name, propertySupport);
		this.defaultValue = defaultValue;
	}

	public BooleanProperty(final String name, final IPropertiesGroup propertySupport) {
		this(name, propertySupport, false);
	}

	@Override
	public IChainBuilderFactory<Boolean> createBindingChain() {
		return new BindingChain(this, errorNotifier).bindProperty(this::setObjectValueFromComponent);
	}

	@SafeVarargs
	@Override
	public final BooleanProperty configureTyped(final Consumer<AbstractTypedProperty<Boolean>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(final Object caller, final boolean newValue) {
		setObjectValue(caller, newValue);
	}

	@Override
	protected Boolean replaceValue(final Boolean newValue) {
		if (newValue == null) {
			throw new IllegalArgumentException("Null value is not allowed");
		}
		final var oldValue = value;
		value = newValue;
		return oldValue;
	}

	@Override
	public Boolean getObjectValue() {
		return getValue();
	}

	@Override
	public void reset(final Object caller) {
		setValue(this, defaultValue);
	}

	@Override
	public void flush() {
		super.flush();
		propertySupport.getChangeSupport().firePropertyChange(getName(), this, null, value);
	}

}
