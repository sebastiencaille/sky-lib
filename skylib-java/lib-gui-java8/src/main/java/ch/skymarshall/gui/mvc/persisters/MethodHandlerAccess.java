package ch.skymarshall.gui.mvc.persisters;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import ch.skymarshall.gui.mvc.properties.IPersister;

public class MethodHandlerAccess<A> implements IPersisterFactory<A> {

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

	public IPersister<A> asPersister(final Object object) {
		return new IPersister<A>() {
			@Override
			public A get() {
				try {
					return (A) getter.bindTo(object).invoke();
				} catch (Throwable e) {
					throw new IllegalStateException("Cannot invoke getter", e);
				}
			}

			@Override
			public void set(final A value) {
				try {
					setter.bindTo(object).invoke(value);
				} catch (Throwable e) {
					throw new IllegalStateException("Cannot invoke getter", e);
				}
			}
		};
	}

	public static <U> MethodHandlerAccess<U> objectAccess(final Field field) {
		return new MethodHandlerAccess<>(field);
	}

}
