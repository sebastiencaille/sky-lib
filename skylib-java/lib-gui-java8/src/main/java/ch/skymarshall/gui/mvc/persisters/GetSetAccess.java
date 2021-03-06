package ch.skymarshall.gui.mvc.persisters;

import java.util.function.BiConsumer;
import java.util.function.Function;

import ch.skymarshall.gui.mvc.properties.IPersister;

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
	public IPersister<A> asPersister(final Object object) {
		return new IPersister<A>() {
			@Override
			public A get() {
				return getter.apply((T) object);
			}

			@Override
			public void set(final A value) {
				setter.accept((T) object, value);
			}
		};
	}
}
