package ch.scaille.util.dao.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import ch.scaille.annotations.Persistency;

/**
 * This class allows to access a public field attribute
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
@SuppressWarnings("java:S3011")
public class FieldAttribute<T> extends AbstractAttributeMetaData<T> {

	private final Field field;
	private final boolean readOnly;

	public FieldAttribute(final String name, final Field field) {
		super(name, field.getType());
		this.field = field;
		this.field.setAccessible(true);

		final var persistency = getAnnotation(Persistency.class);
		readOnly = Modifier.isFinal(field.getModifiers()) || (persistency != null && persistency.readOnly());
	}

	@Override
	public Object getValueOf(final T from) {
		try {
			return field.get(from);
		} catch (final Exception e) {
			throw new IllegalStateException("Unable to get object", e);
		}

	}

	@Override
	public void setValueOf(final T to, final Object value) {
		if (isReadOnly()) {
			throw new IllegalStateException("Attribute " + getName() + " is read-only");
		}
		try {
			field.set(to, value);
		} catch (final Exception e) {
			throw new IllegalStateException("Unable to set object", e);
		}

	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public <A extends Annotation> A getAnnotation(final Class<A> annotation) {
		return field.getAnnotation(annotation);
	}

	@Override
	public Class<?> getDeclaringType() {
		return field.getDeclaringClass();
	}

	@Override
	public Type getGenericType() {
		return field.getGenericType();
	}

	@Override
	public String getCodeName() {
		return field.getName();
	}

	@Override
	public int getModifier() {
		return field.getModifiers();
	}

	@Override
	public String toString() {
		return getName() + "(" + getType() + ")";
	}
}
