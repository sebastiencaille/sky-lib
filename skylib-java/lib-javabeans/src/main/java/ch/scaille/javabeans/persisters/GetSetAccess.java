package ch.scaille.javabeans.persisters;

import java.util.function.BiConsumer;
import java.util.function.Function;

import ch.scaille.javabeans.properties.IPersister;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 *
 * @param <T> Type of the container object
 * @param <A> Type of the object's attribute
 * @author scaille
 */
@NullMarked
public class GetSetAccess<T, A extends @Nullable Object> implements IPersisterFactory<T, A> {

    private final Function<T, A> getter;
    @Nullable
    private final BiConsumer<T, A> setter;

    public GetSetAccess(final Function<T, A> getter, @Nullable final BiConsumer<T, A> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public IPersister<A> asPersister(final IObjectProvider<T> objectProvider) {
        return new IPersister<>() {
            @Override
            public A get() {
                return getter.apply(objectProvider.getObject());
            }

            @Override
            public void set(final A value) {
                if (setter != null) {
                    setter.accept(objectProvider.getObject(), value);
                }
            }
        };
    }
}
