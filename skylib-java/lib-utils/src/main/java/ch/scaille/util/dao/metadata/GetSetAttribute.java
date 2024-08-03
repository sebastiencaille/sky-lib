package ch.scaille.util.dao.metadata;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;

import ch.scaille.annotations.Persistency;

/**
 * This class allows accessing an attribute through its get/set methods
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class GetSetAttribute<T, V> extends AbstractAttributeMetaData<T, V> {

	protected final MethodHandle getter;
	protected final MethodHandle setter;
	protected final Method attributeGetterInfo;

	public GetSetAttribute(final String name, Method attributeGetterInfo, final MethodHandle getter,
			final MethodHandle setter) {
		super(name, (Class<V>) getter.type().returnType());
		this.attributeGetterInfo = attributeGetterInfo;
		this.getter = getter;
		this.setter = setter;
	}

	public MethodHandle getGetter() {
		return getter;
	}

	public MethodHandle getSetter() {
		return setter;
	}

	@Override
	public Object getValueOf(final T from) {
		try {
			return getter.bindTo(from).invoke();
		} catch (final Throwable e) {
			throw new IllegalStateException("Unable to get object", e);
		}
	}

	@Override
	public void setValueOf(final T to, final Object value) {
		try {
			setter.bindTo(to).invoke(value);
		} catch (final Throwable e) {
			throw new IllegalStateException("Unable to set object", e);
		}
	}

	@Override
	public boolean isReadOnly() {
		if (setter == null) {
			return true;
		}
		return getAnnotation(Persistency.class).map(Persistency::readOnly).orElse(false);
	}

	@Override
	public <A extends Annotation> Optional<A> getAnnotation(final Class<A> annotation) {
		return Optional.ofNullable(attributeGetterInfo.getAnnotation(annotation));
	}

	@Override
	public Class<T> getDeclaringType() {
		return (Class<T>) attributeGetterInfo.getDeclaringClass();
	}

	@Override
	public Type getGenericType() {
		return attributeGetterInfo.getGenericReturnType();
	}

	@Override
	public String getCodeName() {
		return getName();
	}

	@Override
	public int getModifier() {
		return attributeGetterInfo.getModifiers();
	}

	@Override
	public String toString() {
		return name + '(' + type.getName() + ')';
	}
}
