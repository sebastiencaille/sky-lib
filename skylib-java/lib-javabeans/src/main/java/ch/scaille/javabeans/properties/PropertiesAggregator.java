package ch.scaille.javabeans.properties;

import ch.scaille.javabeans.*;
import ch.scaille.javabeans.chain.BindingChain;
import org.jspecify.annotations.NonNull;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Allows trigger {@link IPropertyEventListener} when the value of a property
 * in this group is changed
 *
 * @author Sebastien Caille
 *
 */
public class PropertiesAggregator<T> extends AbstractProperty {

    private int callCount = 0;

    private Supplier<T> evaluator = () -> null;

    public PropertiesAggregator(String name, IPropertiesGroup group) {
        super(name, group);
    }

    protected PropertiesAggregator(final String name, final IPropertiesOwner model) {
        super(name, model.getPropertySupport());
    }

    @Override
    public void reset(@NonNull final Object caller) {
        // noop
    }

    @Override
    public void load(@NonNull final Object caller) {
        // noop
    }

    @Override
    public void save() {
        // noop
    }

    @Override
    public void flushChanges(@NonNull final Object caller) {
        propertySupport.getChangeSupport().firePropertyChange(getName(), this, null, evaluator.get());
    }

    @NonNull
    @Override
    public PropertyChangeEvent getRefreshChangeEvent() {
        return new PropertyChangeEvent(this, getName(), null, null);
    }


    // ****************** 1 properties (for x properties + 1), final evaluator
    public interface Applier1<P1, R> {
        R apply(P1 property1);
    }

    public <P1 extends AbstractProperty> PropertiesAggregator<T> add(P1 property1, Applier1<P1, T> applier) {
        register(property1);
        this.evaluator = () -> applier.apply(property1);
        return this;
    }

    // ****************** 2 properties, final evaluator

    public interface Applier2<P1, P2, R> {
        R apply(P1 property1, P2 property2);
    }

    public <P1 extends AbstractProperty, P2 extends AbstractProperty> PropertiesAggregator<T>
    add(P1 property1, P2 property2, Applier2<P1, P2, T> applier) {
        register(property1, property2);
        this.evaluator = () -> applier.apply(property1, property2);
        return this;
    }

    // ****************** 3 properties, final evaluator

    public interface Applier3<P1, P2, P3, R> {
        R apply(P1 property1, P2 property2, P3 property3);
    }

    public <P1 extends AbstractProperty, P2 extends AbstractProperty, P3 extends AbstractProperty> PropertiesAggregator<T>
    add(P1 property1, P2 property2, P3 property3, Applier3<P1, P2, P3, T> applier) {
        register(property1, property2, property3);
        this.evaluator = () -> applier.apply(property1, property2, property3);
        return this;
    }

    // ****************** 4 properties, final evaluator

    public interface Applier4<P1, P2, P3, P4, R> {
        R apply(P1 property1, P2 property2, P3 property3, P4 property4);
    }

    public <P1 extends AbstractProperty, P2 extends AbstractProperty, P3 extends AbstractProperty, P4 extends AbstractProperty>
    PropertiesAggregator<T> add(P1 property1, P2 property2, P3 property3, P4 property4, Applier4<P1, P2, P3, P4, T> applier) {
        register(property1, property2, property3, property4);
        this.evaluator = () -> applier.apply(property1, property2, property3, property4);
        return this;
    }


    // ****************** 4 properties, and more

    public interface Applier4More<P1, P2, P3, P4, R> {
        PropertiesAggregator<R> apply(P1 property1, P2 property2, P3 property3, P4 property4, PropertiesAggregator<R> group);
    }

    public <P1 extends AbstractProperty, P2 extends AbstractProperty, P3 extends AbstractProperty, P4 extends AbstractProperty>
    PropertiesAggregator<T> addWithMore(P1 property1, P2 property2, P3 property3, P4 property4,
                                        Applier4More<P1, P2, P3, P4, T> applier) {
        register(property1, property2, property3, property4);
        applier.apply(property1, property2, property3, property4, this);
        return this;
    }

    private void register(AbstractProperty... properties) {
        Arrays.stream(properties).forEach(p -> p.addListener(impl));
    }

    private class Impl implements IPropertyEventListener {
        @Override
        public void propertyModified(@NonNull final Object caller, final PropertyEvent event) {
            switch (event.kind()) {
                case BEFORE:
                    if (callCount > 0) {
                        return;
                    }
                    callCount++;
                    break;

                case AFTER:
                    callCount--;
                    if (callCount != 0) {
                        return;
                    }
                    flushChanges(this);
                    break;
                default:
                    break;
            }

        }
    }

    private final Impl impl = new Impl();

    /**
     * Executes binding when the property is updated (transmitMode = BOTH only)
     */
    public IBindingController listenActive(final Consumer<T> binding) {
        final var listen = createBindingChain().listen(binding);
        listen.getVetoer().inhibitTransmitToComponentWhen(p -> p.getTransmitMode() != IVetoer.TransmitMode.TRANSMIT);
        return listen;
    }

    /**
     * Executes binding when the property is updated (transmitMode =
     * BOTH|TO_COMPONENT)
     */
    @NonNull
    public IBindingController listen(final @NonNull Consumer<T> binding) {
        return createBindingChain().listen(binding);
    }

    @NonNull
    public IChainBuilderFactory<T> createBindingChain() {
        return new BindingChain(this, errorNotifier).bindProperty((caller, value) -> {});
    }

}
