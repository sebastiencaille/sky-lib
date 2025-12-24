package ch.scaille.javabeans.properties;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Allows providing a context and firing changes from other properties
 * 
 * @param <T> the record type
 */
@NullMarked
public record PropertiesContext<T>(T object, List<AbstractProperty> properties) {

	public static <T extends AbstractProperty> PropertiesContext<T> ofProperty(T property) {
		return new PropertiesContext<>(property, List.of(property));
	}
	
	public static <T> PropertiesContext<T> ofRecord(T propertiesRecord) {
		return new PropertiesContext<>(propertiesRecord, Arrays.stream(propertiesRecord.getClass().getRecordComponents())
				.map(field -> asProperty(propertiesRecord, field))
				.filter(Objects::nonNull)
				.toList());
	}

	@Nullable
	private static AbstractProperty asProperty(Object propsRecord, RecordComponent component) {
		try {
			final var accessor = component.getAccessor();
			accessor.setAccessible(true);
			return (AbstractTypedProperty<?>) accessor.invoke(propsRecord);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			// ignore
			return null;
		}
	}

}
