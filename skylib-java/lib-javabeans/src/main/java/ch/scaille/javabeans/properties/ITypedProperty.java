package ch.scaille.javabeans.properties;

import org.jspecify.annotations.Nullable;

import ch.scaille.javabeans.IChainBuilder;
import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.converters.IConverter;

public interface ITypedProperty<T extends @Nullable Object>
    extends IProperty, IChainBuilder<T> {

    void setPersister(IPersister<T> persister);

    void addImplicitConverter(IConverter<T, T> converter);

    IChainBuilderFactory<T> createBindingChain();


}
