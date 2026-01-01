package ch.scaille.javabeans.properties;

import ch.scaille.javabeans.*;
import ch.scaille.javabeans.chain.BindingChain;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.javabeans.converters.IConverterWithContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.beans.PropertyChangeEvent;
import java.util.IdentityHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Allows trigger {@link IPropertyEventListener} when the value of a property
 * in this group is changed
 *
 * @author Sebastien Caille
 *
 */
@NullMarked
public class PropertiesAggregator<T extends @Nullable Object> extends AbstractProperty implements IChainBuilder<T> {

    private class Snapshot<P extends @Nullable Object> implements Supplier<P> {
        @Nullable
        private P value;

        private Snapshot(IChainBuilder<P> chain) {
            notSet.put(this, this);
            chain.listen(v -> {
                try {
                    impl.propertyModified(PropertyEvent.EventKind.BEFORE);
                    value = v;
                    if (!notSet.isEmpty()) {
                        // Startup recording. We trigger the binding once all the data are available.
                        notSet.remove(this);
                    }
                } finally {
                    impl.propertyModified(PropertyEvent.EventKind.AFTER);
                }
            });
        }

        @Nullable
        public P get() {
            return value;
        }
    }

    public <K> PropertiesAggregator<T> of(PropertiesContext<K> context, Function<K, T> applier) {
        this.evaluator = () -> applier.apply(context.object());
        context.properties().forEach(p -> p.addListener((c, event) -> impl.propertyModified(event.kind())));
        return this;
    }


    // ****************** 1 properties (for x properties + 1), final evaluator
    public interface Applier1<T1, R extends @Nullable Object> {
        R apply(Supplier<T1> value1);
    }

    public <T1> PropertiesAggregator<T> add(IChainBuilder<T1> chain1, Applier1<T1, T> applier) {
        final var value1 = new Snapshot<>(chain1);
        this.evaluator = () -> applier.apply(value1);
        return this;
    }

    // ****************** 2 properties, final evaluator

    public interface Applier2<T1, T2, R extends @Nullable Object> {
        R apply(Supplier<T1> value1, Supplier<T2> value2);
    }

    public <T1, T2> PropertiesAggregator<T>
    add(IChainBuilder<T1> chain1, IChainBuilder<T2> chain2, Applier2<T1, T2, T> applier) {
        final var value1 = new Snapshot<>(chain1);
        final var value2 = new Snapshot<>(chain2);
        this.evaluator = () -> applier.apply(value1, value2);
        return this;
    }

    // ****************** 3 properties, final evaluator

    public interface Applier3<T1, T2, T3, R extends @Nullable Object> {
        R apply(Supplier<T1> property1, Supplier<T2> value2, Supplier<T3> value3);
    }

    public <T1, T2, T3> PropertiesAggregator<T>
    add(IChainBuilder<T1> chain1, IChainBuilder<T2> chain2, IChainBuilder<T3> chain3,
        Applier3<T1, T2, T3, T> applier) {
        final var value1 = new Snapshot<>(chain1);
        final var value2 = new Snapshot<>(chain2);
        final var value3 = new Snapshot<>(chain3);
        this.evaluator = () -> applier.apply(value1, value2, value3);
        return this;
    }

    // ****************** 4 properties, final evaluator

    public interface Applier4<T1, T2, T3, T4, R extends @Nullable Object> {
        R apply(Supplier<T1> value1, Supplier<T2> value2, Supplier<T3> value3, Supplier<T4> value4);
    }

    public <T1, T2 extends IChainBuilder<T2>, T3 extends IChainBuilder<T3>, T4 extends IChainBuilder<T4>>
    PropertiesAggregator<T> add(IChainBuilder<T1> chain1, IChainBuilder<T2> chain2, IChainBuilder<T3> chain3, IChainBuilder<T4> chain4,
                                Applier4<T1, T2, T3, T4, T> applier) {
        final var value1 = new Snapshot<>(chain1);
        final var value2 = new Snapshot<>(chain2);
        final var value3 = new Snapshot<>(chain3);
        final var value4 = new Snapshot<>(chain4);
        this.evaluator = () -> applier.apply(value1, value2, value3, value4);
        return this;
    }


    // ****************** 4 properties, and more

    public interface Applier4More<T1, T2, T3, T4, R extends @Nullable Object> {
        PropertiesAggregator<R> apply(Supplier<T1> property1, Supplier<T2> value2, Supplier<T3> value3, Supplier<T4> value4, PropertiesAggregator<R> group);
    }

    public <T1, T2, T3, T4>
    PropertiesAggregator<T> addWithMore(IChainBuilder<T1> chain1, IChainBuilder<T2> chain2, IChainBuilder<T3> chain3, IChainBuilder<T4> chain4,
                                        Applier4More<T1, T2, T3, T4, T> applier) {
        final var value1 = new Snapshot<>(chain1);
        final var value2 = new Snapshot<>(chain2);
        final var value3 = new Snapshot<>(chain3);
        final var value4 = new Snapshot<>(chain4);
        applier.apply(value1, value2, value3, value4, this);
        return this;
    }

    private class Impl {

        public void propertyModified(PropertyEvent.EventKind kind) {
            switch (kind) {
                case BEFORE:
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

    private int callCount = 0;

    private transient Supplier<T> evaluator = () -> null;

    private final transient Impl impl = new Impl();

    private final IdentityHashMap<Snapshot<?>, Snapshot<?>> notSet = new IdentityHashMap<>();


    public PropertiesAggregator(String name, IPropertiesGroup group) {
        super(name, group);
    }

    public PropertiesAggregator(final String name, final IPropertiesOwner model) {
        super(name, model.getPropertySupport());
    }

    @Override
    public void reset(final Object caller) {
        // noop
    }

    @Override
    public void load(final Object caller) {
        // noop
    }

    @Override
    public void save() {
        // noop
    }

    @Override
    public void flushChanges(final Object caller) {
        if (notSet.isEmpty()) {
            propertySupport.getChangeSupport().firePropertyChange(getName(), this, null, evaluator.get());
        }
    }

   
    @Override
    public PropertyChangeEvent getRefreshChangeEvent() {
        return new PropertyChangeEvent(this, getName(), null, null);
    }

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
   
    public IBindingController listen(final Consumer<T> binding) {
        return createBindingChain().listen(binding);
    }

    @Override
    public <C> IChainBuilder<C> bind(IConverter<T, C> converter) {
        return createBindingChain().bind(converter);
    }

    @Override
    public <C> IChainBuilder<C> bind(Function<T, C> prop2Comp, Function<C, T> comp2Prop) {
        return createBindingChain().bind(prop2Comp, comp2Prop);
    }

    @Override
    public <C> IChainBuilder<C> listenF(Function<T, C> prop2Comp) {
        return createBindingChain().listenF(prop2Comp);
    }

    @Override
    public <C, K> IChainBuilder<C> bind(IConverterWithContext<T, C, K> converter) {
        return createBindingChain().bind(converter);
    }

    @Override
    public <C, K> IChainBuilder<C> bind(PropertiesContext<K> multiProperties, BiFunction<T, K, C> prop2Comp, BiFunction<C, K, T> comp2Prop) {
        return createBindingChain().bind(multiProperties, prop2Comp, comp2Prop);
    }

    @Override
    public <C, K> IChainBuilder<C> listen(PropertiesContext<K> multiProperties, BiFunction<T, K, C> prop2Comp) {
        return createBindingChain().listen(multiProperties, prop2Comp);
    }

    public IBindingController bind(final IComponentBinding<T> binding) {
        return createBindingChain().bind(binding);
    }


    private IChainBuilderFactory<T> createBindingChain() {
        return new BindingChain(this, errorNotifier).bindProperty((caller, value) -> {
            // nothing to set
        });
    }

}
