package ch.scaille.javabeans.persisters;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import ch.scaille.javabeans.properties.IPersister;
import org.jspecify.annotations.NullMarked;

/**
 * To access a property using a getter and a setter
 */
@NullMarked
public class MethodHandlerAccess<T, A> implements IPersisterFactory<T, A> {

	private final MethodHandle getter;
	private final MethodHandle setter;

	public MethodHandlerAccess(final Field field) {
		try {
			this.getter = MethodHandles.lookup().unreflectGetter(field);
			this.setter = MethodHandles.lookup().unreflectSetter(field);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Cannot initialize invoker", e);
		}
	}

	@Override
	public IPersister<A> asPersister(final IObjectProvider<T> objectProvider) {
		return new IPersister<>() {
			@Override
			public A get() {
				try {
					return (A) getter.bindTo(objectProvider.getObject()).invoke();
				} catch (Throwable e) {
					throw new IllegalStateException("Cannot invoke getter", e);
				}
			}

			@Override
			public void set(final A value) {
				try {
					setter.bindTo(objectProvider.getObject()).invoke(value);
				} catch (Throwable e) {
					throw new IllegalStateException("Cannot invoke getter", e);
				}
			}
		};
	}

}
