package ch.scaille.javabeans.persisters;

import java.util.function.BiConsumer;
import java.util.function.Function;

import ch.scaille.javabeans.properties.IPersister;

/**
 *
 * @author scaille
 *
 * @param <T> Type of the container object
 * @param <A> Type of the object's attribute
 */
public class GetSetAccess<T, A> implements IPersisterFactory<T, A> {

	private final Function<T, A> getter;
	private final BiConsumer<T, A> setter;

	public GetSetAccess(final Function<T, A> getter, final BiConsumer<T, A> setter) {
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
				setter.accept(objectProvider.getObject(), value);
			}
		};
	}
}
