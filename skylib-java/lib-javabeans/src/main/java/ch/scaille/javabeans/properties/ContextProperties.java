package ch.scaille.javabeans.properties;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Allows to provide a context and firing changes from other properties 
 * 
 * @param <T> the record type
 */
public record ContextProperties<T>(T object, List<AbstractProperty> properties) {

	public static <T extends AbstractProperty> ContextProperties<T> ofProperty(T property) {
		return new ContextProperties<>(property, List.of(property));
	}
	
	public static <T> ContextProperties<T> ofRecord(T propertiesRecord) {
		return new ContextProperties<>(propertiesRecord, Arrays.stream(propertiesRecord.getClass().getRecordComponents())
				.map(field -> asProperty(propertiesRecord, field))
				.filter(Objects::nonNull)
				.toList());
	}

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
