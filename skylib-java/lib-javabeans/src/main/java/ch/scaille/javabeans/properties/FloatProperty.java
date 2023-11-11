package ch.scaille.javabeans.properties;

import java.util.function.Consumer;

import ch.scaille.javabeans.BindingChain;
import ch.scaille.javabeans.BindingChain.EndOfChain;
import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.IPropertiesGroup;

/**
 * Property containing a float value.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public class FloatProperty extends AbstractTypedProperty<Float> {

	private float value;
	private final float defaultValue;

	public FloatProperty(final String name, final IPropertiesOwner model, final float defaultValue) {
		super(name, model);
		this.defaultValue = defaultValue;
	}

	public FloatProperty(final String name, final IPropertiesOwner model) {
		this(name, model, 0.0f);
	}

	public FloatProperty(final String name, final IPropertiesGroup propertySupport, final float defaultValue) {
		super(name, propertySupport);
		this.defaultValue = defaultValue;
	}

	public FloatProperty(final String name, final IPropertiesGroup propertySupport) {
		this(name, propertySupport, 0.0f);
	}

	@Override
	public EndOfChain<Float> createBindingChain() {
		return new BindingChain(this, errorNotifier).bindProperty(this::setObjectValueFromComponent);
	}

	@SafeVarargs
	@Override
	public final FloatProperty configureTyped(final Consumer<AbstractTypedProperty<Float>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
	}

	public float getValue() {
		return value;
	}

	public void setValue(final Object caller, final float newValue) {
		setObjectValue(caller, newValue);
	}

	@Override
	protected Float replaceValue(final Float newValue) {
		if (newValue == null) {
			throw new IllegalArgumentException("Null value is not allowed");
		}
		final var oldValue = value;
		value = newValue;
		return oldValue;
	}

	@Override
	public Float getObjectValue() {
		return getValue();
	}

	@Override
	public void attach() {
		super.attach();
		propertySupport.getChangeSupport().firePropertyChange(getName(), this, null, value);
	}

	@Override
	public void reset(final Object caller) {
		setValue(this, defaultValue);
	}

}
