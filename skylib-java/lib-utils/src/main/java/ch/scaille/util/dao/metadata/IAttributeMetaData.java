package ch.scaille.util.dao.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IAttributeMetaData<T> {

	String getName();

	boolean isReadOnly();

	<A extends Annotation> Optional<A> getAnnotation(Class<A> annotation);

	Object getValueOf(T object);

	void setValueOf(T object, Object o);

	void copy(T from, T to);

	Class<T> getDeclaringType();

	Type getGenericType();

	String getCodeName();

	<V> void onTypedMetaDataC(Consumer<AbstractAttributeMetaData<T, V>> consumer);

	<V, R> R onTypedMetaDataF(Function<AbstractAttributeMetaData<T, V>, R> function);

	/**
	 * Tests if the attribute is applied on a given type
	 * @param targetType
	 * @return true if the type matches
	 */
	boolean isOnType(Class<?> targetType);
	
	/**
	 * 
	 * @param <V>
	 * @param targetType
	 * @return
	 */
	<V> AbstractAttributeMetaData<T, V> unwrap(Class<V> targetType);

	

}
