package ch.scaille.javabeans.chain;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

import ch.scaille.javabeans.properties.PropertiesContext;
import ch.scaille.javabeans.DependenciesBuildingReport;
import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.IChainBuilder;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.Logging;
import ch.scaille.javabeans.converters.ConversionException;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.javabeans.converters.IConverterWithContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Chain builder
 *
 * @param <P> The property side type
 */
@NullMarked
public class EndOfChain<P extends @Nullable Object> implements IChainBuilder<P> {

    /**
     * Link that targets the component
     */
    private final class LinkToComponent implements Link<P, P> {

        private final IComponentBinding<P> newBinding;

        private LinkToComponent(final IComponentBinding<P> newBinding) {
            this.newBinding = newBinding;

            newBinding.addComponentValueChangeListener(new IComponentLink<>() {
                @Override
                public void setValueFromComponent(final Object component, @Nullable final P componentValue) {
                    if (!chain.mustSendToProperty(chain)) {
                        return;
                    }
                    Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE,
                            () -> "Component change: " + component.getClass().getSimpleName() + ": " + componentValue);
                    chain.propagateComponentChange(component, componentValue);
                }

                @Override
                public void unbind() {
                    newBinding.removeComponentValueChangeListener();
                }

                @Override
                public void reloadComponentValue() {
                    chain.transmitChangesBothWays();
                }
            });
        }

        @Override
        public P toComponent(final P value) {
            Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Setting component value: " + value);
            newBinding.setComponentValue(chain.getProperty(), value);
            return value;
        }

        @Override
        public P toProperty(final Object component, final P value) {
            return value;
        }

        @Override
        public void unbind() {
            newBinding.removeComponentValueChangeListener();
        }

    }

    protected final IBindingChainModifier chain;

    public EndOfChain(IBindingChainModifier chain) {
        this.chain = chain;
    }

    private IllegalStateException prop2CompOnlyException() {
        return new IllegalStateException("Binding cannot read component values");
    }

    @Override
    public IBindingController listen(final Consumer<P> newBinding) {
        chain.addLink(link(v -> {
            newBinding.accept(v);
            return null;
        }, v -> {
            throw prop2CompOnlyException();
        }));
        return chain;
    }

    @Override
    public IBindingController bind(final IComponentBinding<P> newBinding) {
        chain.addLink(new LinkToComponent(newBinding));
        DependenciesBuildingReport.addDependency(chain.getProperty(), newBinding);
        return chain;
    }

    /**
     * @param <C> The component side type
     */
    @Override
    public <C extends @Nullable Object> EndOfChain<C> bind(final IConverter<P, C> converter) {
        converter.initialize(chain.getProperty());
        chain.addLink(link(converter::convertPropertyValueToComponentValue,
                converter::convertComponentValueToPropertyValue));
        return new EndOfChain<>(chain);
    }

    /**
     * @param <C> The component side type
     */
    @Override
    public <C extends @Nullable Object> EndOfChain<C> bind(final Function<P, C> prop2Comp, final Function<C, P> comp2Prop) {
        chain.addLink(link(prop2Comp::apply, comp2Prop::apply));
        return new EndOfChain<>(chain);
    }

    /**
     * @param <C> The component side type
     */
    @Override
    public <C extends @Nullable Object> EndOfChain<C> listenF(final Function<P, C> prop2Comp) {
        chain.addLink(link(prop2Comp::apply, value -> {
            throw new ConversionException("Read only");
        }));
        return new EndOfChain<>(chain);
    }

    /**
     * @param <C> The component side type
     */
    @Override
    public <C extends @Nullable Object, K> EndOfChain<C> bind(final IConverterWithContext<P, C, K> converter) {
        converter.initialize(chain.getProperty());
        final var contextProperties = converter.contextProperties();
        register(contextProperties);
        chain.addLink(link(
                value -> converter.convertPropertyValueToComponentValue(value, contextProperties.object()),
                value -> converter.convertComponentValueToPropertyValue(value, contextProperties.object())));
        return new EndOfChain<>(chain);
    }

    /**
     * @param <C> The component side type
     */
    @Override
    public <C extends @Nullable Object, K> EndOfChain<C> bind(final PropertiesContext<K> multiProperties,
                                                              final BiFunction<P, K, C> prop2Comp,
                                                              final BiFunction<C, K, @Nullable P> comp2Prop) {
        register(multiProperties);
        chain.addLink(link(
                value -> prop2Comp.apply(value, multiProperties.object()),
                value -> comp2Prop.apply(value, multiProperties.object())));
        return new EndOfChain<>(chain);
    }

    /**
     * @param <C> The component side type
     */
    @Override
    public <C extends @Nullable Object, K> EndOfChain<C> listen(final PropertiesContext<K> multiProperties, final BiFunction<P, K, C> prop2Comp) {
        register(multiProperties);
        chain.addLink(link(
                value -> prop2Comp.apply(value, multiProperties.object()),
                value -> {
                    throw new ConversionException("Read only");
                }));
        return new EndOfChain<>(chain);
    }


    private void register(PropertiesContext<?> multiProperties) {
        multiProperties.properties().forEach(p -> p.addListener(e -> chain.flushChanges()));
    }


    protected <C extends @Nullable Object> Link<P, C> link(final ConversionFunction<P, C> prop2Comp, final ConversionFunction<C, P> comp2Prop) {
        return new Link<>() {

            @Override
            public C toComponent(final P value) throws ConversionException {
                return prop2Comp.apply(value);
            }

            @Override
            public P toProperty(final Object component, final C value) throws ConversionException {
                return comp2Prop.apply(value);
            }

            @Override
            public void unbind() {
                // nothing to do
            }

        };
    }

}