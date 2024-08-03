package ch.scaille.util.dao.metadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.scaille.util.dao.metadata.AttributeFactory.Mode;

/**
 * Contains all the DO's meta-data.
 * <p>
 *
 *
 * @param <T> a data type
 */
public class AbstractObjectMetaData<T> {

	/**
	 * Object attributes
	 */
	protected final Map<String, IAttributeMetaData<T>> attributes = new HashMap<>();

	/**
	 * Type of the object
	 */
	private final Class<? extends T> dataType;

	/**
	 * Access mode
	 */
	private Mode attributeMode = Mode.AUTOMATIC;

	protected AbstractObjectMetaData(final Class<? extends T> clazz) {
		this(clazz, false);
	}

	protected AbstractObjectMetaData(final Class<? extends T> clazz, final boolean accessPrivateFields) {
		dataType = clazz;
		if (accessPrivateFields) {
			attributeMode = Mode.FIELD;
		}
		introspectClass((Class<? super T>) clazz, accessPrivateFields);
	}

	protected AbstractObjectMetaData(final Class<? extends T> clazz, final Set<String> attribNames) {
		dataType = clazz;
		createAttributesMetaData((Class<? super T>) clazz, attribNames);
	}

	public Class<? extends T> getDataType() {
		return dataType;
	}

	public Collection<IAttributeMetaData<T>> getAttributes() {
		return new HashSet<>(attributes.values());
	}

	public DataObjectManager<T> createAccessorTo(final T object) {
		return new DataObjectManager<>(this, object);
	}

	public UntypedDataObjectManager createUntypedAccessorTo(final T object) {
		return new UntypedDataObjectMetaData(dataType).createUntypedObjectAccessorFor(object);
	}

	public UntypedDataObjectMetaData createUntypedMetaData() {
		return new UntypedDataObjectMetaData(dataType);
	}

	protected void introspectClass(final Class<? super T> clazz, final boolean accessPrivateFields) {

		final var attribNames = new HashSet<String>();

		scanMethods(clazz, attribNames);
		scanFields(clazz, accessPrivateFields, attribNames);

		createAttributesMetaData(clazz, attribNames);
		if (clazz.getSuperclass() != null && !Object.class.equals(clazz.getSuperclass())) {
			introspectClass(clazz.getSuperclass(), accessPrivateFields);
		}

	}

	private void scanFields(final Class<?> clazz, final boolean accessPrivateFields, final Set<String> attribNames) {
		for (final var field : clazz.getDeclaredFields()) {
			final var canAccess = Modifier.isPublic(field.getModifiers()) || accessPrivateFields;
			if (canAccess && !field.getDeclaringClass().equals(Object.class)) {
				attribNames.add(field.getName());
			}
		}
	}

	private void scanMethods(final Class<?> clazz, final Set<String> attribNames) {
		for (final var method : clazz.getMethods()) {
			final var name = method.getName();
			if (method.getDeclaringClass().equals(Object.class)) {
				continue;
			}
			if (name.startsWith("get") && name.length() > 3 && method.getParameterTypes().length == 0
					&& method.getReturnType() != Void.TYPE) {
				attribNames.add(name.substring(3));
			}
			if (name.startsWith("is") && name.length() > 2 && method.getParameterTypes().length == 0
					&& (method.getReturnType() == Boolean.TYPE || method.getReturnType() == Boolean.class)) {
				attribNames.add(name.substring(2));
			}
		}
	}

	protected <V> void createAttributesMetaData(final Class<? super T> clazz, final Set<String> attribNames) {
		for (final var name : attribNames) {
			final var attribName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			final var access = AttributeFactory.<T, V>create(clazz, name, attribName, attributeMode);
			if (access != null) {
				attributes.put(attribName, access);
			}
		}
	}

	public void removeAttribute(final String ignoredAttribute) {
		if (attributes.remove(ignoredAttribute) == null) {
			throw new IllegalArgumentException("No such attribute:" + ignoredAttribute);
		}
	}

	public boolean hasAttribute(final String name) {
		return attributes.containsKey(name);
	}

	public IAttributeMetaData<T> getAttribute(final String name) {

		final var attrib = attributes.get(name);
		if (attrib == null) {
			throw new IllegalArgumentException("No such attribute:" + name);
		}
		return attrib;
	}

	public void copy(final T from, final T to) {
		for (final var attrib : attributes.values()) {
			if (!attrib.isReadOnly()) {
				attrib.copy(from, to);
			}
		}
	}

	public Collection<String> getAttributeNames() {
		return attributes.keySet();
	}

	public String getName() {
		return getDataType().getName();
	}

	public String getSimpleName() {
		return getDataType().getSimpleName();
	}

	public Constructor<? extends T> getConstructor() throws NoSuchMethodException {
		return getDataType().getConstructor();
	}

	@Override
	public String toString() {
		return "MetaData of " + dataType.getName();
	}
}
