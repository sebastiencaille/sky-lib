package ch.skymarshall.util.dao.metadata;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class NioFieldAttribute<T> extends FieldAttribute<T> {

	private MethodHandle getter;
	private MethodHandle setter;

	public NioFieldAttribute(String name, Field field) {
		super(name, field);
		try {
			getter = MethodHandles.lookup().findGetter(field.getDeclaringClass(), field.getName(), field.getType());
			if (!isReadOnly()) {
				setter = MethodHandles.lookup().findSetter(field.getDeclaringClass(), field.getName(), field.getType());
			} else {
				setter = null;
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException("Cannot find getter/setter", e);
		}
	}

	@Override
	public Object getValueOf(T from) {
		try {
			return getter.bindTo(from).invoke();
		} catch (Throwable e) {
			throw new IllegalStateException("Cannot invoker getter", e);
		}
	}

	@Override
	public void setValueOf(T to, Object value) {
		try {
			if (isReadOnly()) {
				throw new IllegalStateException("Attribute " + getName() + " is read-only");
			}
			setter.bindTo(to).invoke(value);
		} catch (Throwable e) {
			throw new IllegalStateException("Cannot invoker setter", e);
		}
	}

}
