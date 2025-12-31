package ch.scaille.javabeans.persisters;

import java.util.function.BiConsumer;
import java.util.function.Function;

import ch.scaille.javabeans.properties.IPersister;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 *
 * @author scaille
 *
 * @param <T> Type of the container object
 * @param <A> Type of the object's attribute
 */
public class GetSetAccess<T, A> implements IPersisterFactory<@NonNull T, A> {

	private final Function<T, A> getter;
	private final BiConsumer<T, A> setter;

	public GetSetAccess(final Function<T, A> getter, @Nullable final BiConsumer<T, A> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@NonNull
	@Override
	public IPersister<A> asPersister(@NonNull final IObjectProvider<@NonNull T> objectProvider) {
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
