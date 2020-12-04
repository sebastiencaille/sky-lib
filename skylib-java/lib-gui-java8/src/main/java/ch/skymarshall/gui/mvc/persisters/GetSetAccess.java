package ch.skymarshall.gui.mvc.persisters;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import ch.skymarshall.gui.mvc.properties.IPersister;

/**
 *
 * @author scaille
 *
 * @param <C>
 *            Type of the container object
 * @param <A>
 *            Type of the object's attribute
 */
public class GetSetAccess<C, A> implements IPersisterFactory<A> {

	private final Function<C, Supplier<A>> getter;
	private final Function<C, Consumer<A>> setter;

	public GetSetAccess(final Function<C, Supplier<A>> getter, final Function<C, Consumer<A>> setter) {
		this.getter = getter;
		this.setter = setter;

	}

	@Override
	public IPersister<A> asPersister(final Object object) {
		return new IPersister<A>() {
			@Override
			public A get() {
				return getter.apply((C) object).get();
			}

			@Override
			public void set(final A value) {
				final Consumer<A> setterAccess = setter.apply((C) object);
				if (setterAccess != null) {
					setterAccess.accept(value);
				}
			}
		};
	}

	/**
	 * *
	 * 
	 * @param <C>
	 *            Type of the container object
	 * @param <A>
	 *            Type of the object's attribute
	 * 
	 * @param getter
	 * @param setter
	 * @return
	 */
	public static <C, A> GetSetAccess<C, A> access(
			final Function<C, Supplier<A>> getter, final Function<C, Consumer<A>> setter) {
		return new GetSetAccess<>(getter, setter);
	}

}
