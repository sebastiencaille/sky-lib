package ch.scaille.javabeans.properties;

import java.util.Objects;
import java.util.function.Consumer;

import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.chain.BindingChain;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Property containing a float value.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
@NullMarked
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
    public IChainBuilderFactory<Float> createBindingChain() {
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
    protected Float replaceValue(@Nullable final Float newValue) {
        final var oldValue = value;
        value = Objects.requireNonNull(newValue, "Null value is not allowed");
        return oldValue;
    }

    @Override
    public Float getObjectValue() {
        return getValue();
    }

    @Override
    public void reset(final Object caller) {
        setValue(this, defaultValue);
    }

}
