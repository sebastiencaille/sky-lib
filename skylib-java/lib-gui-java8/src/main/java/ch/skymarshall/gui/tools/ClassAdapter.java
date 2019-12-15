package ch.skymarshall.gui.tools;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.AbstractTypedProperty;
import ch.skymarshall.gui.mvc.properties.IPersister;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.util.annotations.Label;
import ch.skymarshall.util.annotations.Ordered;
import ch.skymarshall.util.dao.metadata.AbstractAttributeMetaData;
import ch.skymarshall.util.dao.metadata.DataObjectMetaData;

public class ClassAdapter<T> {

	public static class PropertyEntry<U> {
		private final AbstractTypedProperty<Object> property;
		private final String label;
		private final String tooltip;
		private final AbstractAttributeMetaData<U> metadata;

		public PropertyEntry(final AbstractTypedProperty<Object> property, final AbstractAttributeMetaData<U> metadata,
				final String label, final String tooltip) {
			super();
			this.property = property;
			this.metadata = metadata;
			this.label = label;
			this.tooltip = tooltip;
		}

		public int index() {
			final Ordered annotation = metadata.getAnnotation(Ordered.class);
			if (annotation != null) {
				return annotation.order();
			}
			return Integer.MAX_VALUE / 2;
		}

		public <V> AbstractTypedProperty<V> getProperty(final Class<V> clazz) {
			if (!clazz.equals(getPropertyType())) {
				throw new InvalidParameterException(
						"Expected " + clazz + ", but property type is " + getPropertyType());
			}
			return (AbstractTypedProperty<V>) property;
		}

		public void loadFromObject(final Object caller, final U obj) {
			property.setPersister(new IPersister<Object>() {
				@Override
				public Object get() {
					return metadata.getValueOf(obj);
				}

				@Override
				public void set(final Object value) {
					metadata.setValueOf(obj, value);
				}

			});
			property.load(caller);
		}

		public void saveInCurrentObject() {
			property.save();
		}

		public Class<?> getPropertyType() {
			return metadata.getClassType();
		}

		public String getLabel() {
			return label;
		}

		public String getTooltip() {
			return tooltip;
		}

	}

	private final ResourceBundle bundle;
	private final DataObjectMetaData<T> metaData;

	public ClassAdapter(final Class<T> clazz) {
		this.bundle = null;
		this.metaData = new DataObjectMetaData<>(clazz);
	}

	public ClassAdapter(final ResourceBundle bundle, final Class<T> clazz) {
		this.bundle = bundle;
		this.metaData = new DataObjectMetaData<>(clazz);
	}

	public List<PropertyEntry<T>> getProperties() {
		final IScopedSupport propertySupport = new ControllerPropertyChangeSupport(this).byContainer(this);
		final List<PropertyEntry<T>> properties = new ArrayList<>();
		for (final AbstractAttributeMetaData<T> attrib : metaData.getAttributes()) {

			final String message = findText(attrib, Label::label, ClassAdapter::descriptionKey);
			final String toolTip = findText(attrib, Label::tooltip, ClassAdapter::tooltipKey);
			final ObjectProperty<Object> property = new ObjectProperty<>(attrib.getName(), propertySupport);
			properties.add(new PropertyEntry<>(property, attrib, message, toolTip));
		}
		Collections.sort(properties, (p1, p2) -> Integer.compare(p1.index(), p2.index()));
		propertySupport.attachAll();
		return properties;
	}

	private String findText(final AbstractAttributeMetaData<T> attrib, final Function<Label, String> fromLabel,
			final Function<String, String> nameToKey) {
		final Label label = attrib.getAnnotation(Label.class);
		String value = "";
		if (label != null) {
			value = fromLabel.apply(label);
		}
		if (value.isEmpty() && bundle != null) {
			value = bundle.getString(nameToKey.apply(attrib.getName()));
		}
		return value;
	}

	public static String descriptionKey(final String name) {
		return name + ".description";
	}

	public static String tooltipKey(final String name) {
		return name + ".tooltip";
	}

}
