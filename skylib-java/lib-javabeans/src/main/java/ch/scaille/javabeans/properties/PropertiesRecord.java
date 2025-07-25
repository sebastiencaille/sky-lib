package ch.scaille.javabeans.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;

/**
 * Allows to listen to a record made of properties. 
 * @param <T> the record type
 */
public class PropertiesRecord<T> extends ObjectProperty<T> implements PropertyChangeListener{

	public static <T> PropertiesRecord<T> of(T propertiesRecord, final IPropertiesOwner model) {
		return new PropertiesRecord<>(propertiesRecord, model.getPropertySupport());
	}

	public static <T> PropertiesRecord<T> of(T propertiesRecord, IPropertiesGroup changeSupport) {
		return new PropertiesRecord<>(propertiesRecord, changeSupport);
	}


	public PropertiesRecord(T propsRecord, final IPropertiesGroup model) {
		super(UUID.randomUUID().toString(), model, propsRecord);
		Arrays.stream(propsRecord.getClass().getRecordComponents())
				.map(field -> asProperty(propsRecord, field))
				.filter(Objects::nonNull)
				.forEach(p -> p.addListener(this));
	}

	private AbstractTypedProperty<?> asProperty(T propsRecord, RecordComponent component) {
		try {
			final var accessor = component.getAccessor();
			accessor.setAccessible(true);
			return (AbstractTypedProperty<?>)accessor.invoke(propsRecord);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// ignore
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		this.forceChanged(arg0.getPropertyName());
	}


}
