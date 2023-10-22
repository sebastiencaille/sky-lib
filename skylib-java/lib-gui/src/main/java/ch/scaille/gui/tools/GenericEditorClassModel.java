package ch.scaille.gui.tools;

import static ch.scaille.javabeans.properties.Configuration.persistent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import ch.scaille.annotations.Labeled;
import ch.scaille.annotations.Ordered;
import ch.scaille.javabeans.BindingChain.EndOfChain;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.persisters.Persisters;
import ch.scaille.javabeans.persisters.ObjectProviderPersister.IObjectProvider;
import ch.scaille.javabeans.properties.AbstractTypedProperty;
import ch.scaille.javabeans.properties.ErrorSet;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.util.dao.metadata.AbstractAttributeMetaData;
import ch.scaille.util.dao.metadata.DataObjectMetaData;

public class GenericEditorClassModel<T> implements IGenericEditorModel<T> {

	public static class ClassPropertyEntry<U> extends PropertyEntry {

		private final AbstractAttributeMetaData<U> metadata;

		public ClassPropertyEntry(final AbstractTypedProperty<Object> property,
				final Function<AbstractTypedProperty<?>, EndOfChain<?>> endOfChainProvider,
				final AbstractAttributeMetaData<U> metadata, final boolean readOnly, final String label,
				final String tooltip) {
			super(property, endOfChainProvider, metadata.getClassType(), readOnly, label, tooltip);
			this.metadata = metadata;
		}

		private int index() {
			final var annotation = metadata.getAnnotation(Ordered.class);
			if (annotation != null) {
				return annotation.order();
			}
			return Integer.MAX_VALUE / 2;
		}
	}

	public static class Builder<T> {
		private final Class<T> editedClazz;
		private ResourceBundle bundle = null;
		private boolean readOnly = false;
		private IGenericModelAdapter[] adapters = new IGenericModelAdapter[0];
		private IPropertiesGroup propertySupport;
		private ErrorSet errorSet;

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

		public Builder<T> with(IPropertiesGroup propertySupport) {
			this.propertySupport = propertySupport;
			return this;
		}

		public Builder<T> with(ErrorSet errorSet) {
			this.errorSet = errorSet;
			return this;
		}

		public GenericEditorClassModel<T> build() {
			if (propertySupport == null) {
				propertySupport = PropertyChangeSupportController.mainGroup(this);
			}
			if (errorSet == null) {
				errorSet = new ErrorSet("Error", propertySupport);
			}
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

	@Override
	public IPropertiesGroup getPropertySupport() {
		return config.propertySupport;
	}

	@Override
	public ErrorSet getErrorProperty() {
		return config.errorSet;
	}

	/**
	 * Creates the properties by introspecting the displayed class Class
	 *
	 * @param propertySupport
	 * @param object
	 *
	 * @return
	 */
	@Override
	public List<ClassPropertyEntry<T>> createProperties(IObjectProvider<T> object) {

		final var properties = new ArrayList<ClassPropertyEntry<T>>();
		for (final var attrib : metaData.getAttributes()) {

			final var property = new ObjectProperty<>(attrib.getName(), config.propertySupport);
			property.configureTyped(persistent(object, Persisters.attribute(attrib)));

			final var readOnly = config.readOnly || attrib.isReadOnly();
			final var message = findText(attrib, Labeled::label, PropertyEntry::descriptionKey);
			final var toolTip = findText(attrib, Labeled::tooltip, PropertyEntry::tooltipKey);
			properties.add(
					new ClassPropertyEntry<>(property, this::createBindingChain, attrib, readOnly, message, toolTip));
		}
		properties.sort(Comparator.comparing(ClassPropertyEntry::index));
		return properties;
	}

	private EndOfChain<?> createBindingChain(AbstractTypedProperty<?> property) {
		var chain = property.createBindingChain();
		for (final IGenericModelAdapter adapter : config.adapters) {
			chain = adapter.apply(config.editedClazz, chain);
		}
		return chain;
	}

	private String findText(final AbstractAttributeMetaData<T> attrib, final Function<Labeled, String> fromLabel,
			final UnaryOperator<String> nameToKey) {
		final var label = attrib.getAnnotation(Labeled.class);
		var value = "";
		if (label != null) {
			value = fromLabel.apply(label);
		}
		if (value.isEmpty() && config.bundle != null) {
			value = config.bundle.getString(nameToKey.apply(attrib.getName()));
		}
		return value;
	}

}
