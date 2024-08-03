package ch.scaille.util.dao.metadata;

import java.security.InvalidParameterException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class contains the basic methods and attributes used to access DO's
 * attributes
 *
 * @author Sebastien Caille
 *
 * @param <T>
 * @param <V> value type
 */
public abstract class AbstractAttributeMetaData<T, V> implements IAttributeMetaData<T> {

	protected final String name;

	protected final Class<V> type;

	public abstract int getModifier();

	protected AbstractAttributeMetaData(final String name, final Class<V> type) {
		super();
		this.name = name;
		this.type = type;
	}

	@Override
	public <W> void onTypedMetaDataC(Consumer<AbstractAttributeMetaData<T, W>> consumer) {
		consumer.accept((AbstractAttributeMetaData<T, W>) this);
	}

	@Override
	public <W, R> R onTypedMetaDataF(Function<AbstractAttributeMetaData<T, W>, R> function) {
		return function.apply((AbstractAttributeMetaData<T, W>) this);
	}
	
	@Override
	public boolean isOfType(Class<?> targetType) {
		return targetType.isAssignableFrom(type);
	}
	
	@Override
	public <W> AbstractAttributeMetaData<T, W> unwrap(Class<W> targetType) {
		if (!isOfType(targetType)) {
			throw new InvalidParameterException("Expected type or parent of " + type + ", received " + targetType);
		}
		return (AbstractAttributeMetaData<T, W>) this;
	}
	
	public V get(T from) {
		return (V) getValueOf(from);
	}

	public void set(T object, V value) {
		setValueOf(object, value);
	}
	
	@Override
	public void copy(final T from, final T to) {
		setValueOf(to, getValueOf(from));
	}

	@Override
	public final boolean equals(final Object o) {
		if (!(o instanceof AbstractAttributeMetaData)) {
			return false;
		}
		return name.equals(((AbstractAttributeMetaData<T, V>) o).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Returns the attribute class, preserving the primitive type.
	 * 
	 * @return
	 */
	public Class<V> getType() {
		return type;
	}

	/**
	 * Returns the attribute class. Primitive type are replaced by non-primitive
	 * types
	 * 
	 * @return
	 */
	public Class<V> getClassType() {
		if (!type.isPrimitive()) {
			return type;
		}
		final Class<?> primitiveType;
		if (type == Character.TYPE) {
			primitiveType = Character.class;
		} else if (type == Boolean.TYPE) {
			primitiveType = Boolean.class;
		} else if (type == Short.TYPE) {
			primitiveType = Short.class;
		} else if (type == Integer.TYPE) {
			primitiveType = Integer.class;
		} else if (type == Long.TYPE) {
			primitiveType = Long.class;
		} else if (type == Float.TYPE) {
			primitiveType = Float.class;
		} else if (type == Double.TYPE) {
			primitiveType = Double.class;
		} else {
			throw new IllegalStateException("Unhandled type: " + type);
		}
		return (Class<V>) primitiveType;
	}

}
