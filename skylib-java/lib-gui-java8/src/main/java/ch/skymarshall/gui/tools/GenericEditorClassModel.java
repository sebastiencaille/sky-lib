package ch.skymarshall.gui.tools;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import ch.skymarshall.annotations.Labeled;
import ch.skymarshall.annotations.Ordered;
import ch.skymarshall.gui.mvc.BindingChain.EndOfChain;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.AbstractProperty.ErrorNotifier;
import ch.skymarshall.gui.mvc.properties.AbstractTypedProperty;
import ch.skymarshall.gui.mvc.properties.IPersister;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.util.dao.metadata.AbstractAttributeMetaData;
import ch.skymarshall.util.dao.metadata.DataObjectMetaData;

public class GenericEditorClassModel<T> {

	public static class PropertyEntry<U> {
		private final AbstractTypedProperty<Object> property;
		private final EndOfChain<Object> endOfChain;
		private final AbstractAttributeMetaData<U> metadata;
		private final boolean readOnly;
		private final String label;
		private final String tooltip;

		public PropertyEntry(final AbstractTypedProperty<Object> property, final EndOfChain<Object> endOfChain,
				final AbstractAttributeMetaData<U> metadata, final boolean readOnly, final String label,
				final String tooltip) {
			this.property = property;
			this.endOfChain = endOfChain;
			this.metadata = metadata;
			this.label = label;
			this.tooltip = tooltip;
			this.readOnly = readOnly;
		}

		public int index() {
			final Ordered annotation = metadata.getAnnotation(Ordered.class);
			if (annotation != null) {
				return annotation.order();
			}
			return Integer.MAX_VALUE / 2;
		}

		public <V> EndOfChain<V> getChain(final Class<V> expectedType) {
			if (!expectedType.equals(getPropertyType())) {
				throw new InvalidParameterException(
						"Expected " + expectedType + ", but property type is " + getPropertyType());
			}
			return ((EndOfChain<V>) endOfChain);
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

		public boolean isReadOnly() {
			return readOnly;
		}

		public String getLabel() {
			return label;
		}

		public String getTooltip() {
			return tooltip;
		}

	}

	public static class Builder<T> {
		private final Class<T> editedClazz;
		private ResourceBundle bundle = null;
		private boolean readOnly = false;
		private IGenericModelAdapter[] adapters = new IGenericModelAdapter[0];

		public Builder(final Class<T> clazz) {
			this.editedClazz = clazz;
		}

		public Builder<T> setBundle(final ResourceBundle bundle) {
			this.bundle = bundle;
			return this;
		}

		public Builder<T> setReadOnly(final boolean readOnly) {
			this.readOnly = readOnly;
			return this;
		}

		public Builder<T> addAdapters(final IGenericModelAdapter... adapters) {
			this.adapters = adapters;
			return this;
		}

		public GenericEditorClassModel<T> build() {
			return new GenericEditorClassModel<>(this);
		}
	}

	private final DataObjectMetaData<T> metaData;
	private final Builder<T> config;

	public static <T> Builder<T> builder(final Class<T> clazz) {
		return new Builder<>(clazz);
	}

	protected GenericEditorClassModel(final Builder<T> builder) {
		this.config = builder;
		this.metaData = new DataObjectMetaData<>(builder.editedClazz);
	}

	/**
	 * Creates the properties by introspecting the displayed class Class
	 *
	 * @param errorProperty
	 * @param propertySupport
	 *
	 * @return
	 */
	public List<PropertyEntry<T>> createProperties(final IScopedSupport propertySupport,
			final ErrorNotifier errorNotifier) {

		final List<PropertyEntry<T>> properties = new ArrayList<>();
		for (final AbstractAttributeMetaData<T> attrib : metaData.getAttributes()) {

			final ObjectProperty<Object> property = new ObjectProperty<>(attrib.getName(), propertySupport);
			property.setErrorNotifier(errorNotifier);
			EndOfChain<Object> chain = property.createBindingChain();
			for (final IGenericModelAdapter adapter : config.adapters) {
				chain = adapter.apply(config.editedClazz, chain);
			}

			final boolean readOnly = config.readOnly || attrib.isReadOnly();
			final String message = findText(attrib, Labeled::label, GenericEditorClassModel::descriptionKey);
			final String toolTip = findText(attrib, Labeled::tooltip, GenericEditorClassModel::tooltipKey);
			properties.add(new PropertyEntry<>(property, chain, attrib, readOnly, message, toolTip));
		}
		Collections.sort(properties, (p1, p2) -> Integer.compare(p1.index(), p2.index()));
		return properties;
	}

	private String findText(final AbstractAttributeMetaData<T> attrib, final Function<Labeled, String> fromLabel,
			final UnaryOperator<String> nameToKey) {
		final Labeled label = attrib.getAnnotation(Labeled.class);
		String value = "";
		if (label != null) {
			value = fromLabel.apply(label);
		}
		if (value.isEmpty() && config.bundle != null) {
			value = config.bundle.getString(nameToKey.apply(attrib.getName()));
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
