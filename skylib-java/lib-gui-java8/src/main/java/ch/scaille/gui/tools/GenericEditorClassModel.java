package ch.scaille.gui.tools;

import static ch.scaille.gui.mvc.properties.Configuration.persistent;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import ch.scaille.annotations.Labeled;
import ch.scaille.annotations.Ordered;
import ch.scaille.gui.mvc.BindingChain.EndOfChain;
import ch.scaille.gui.mvc.IScopedSupport;
import ch.scaille.gui.mvc.factories.Persisters;
import ch.scaille.gui.mvc.persisters.ObjectProviderPersister.IObjectProvider;
import ch.scaille.gui.mvc.properties.AbstractTypedProperty;
import ch.scaille.gui.mvc.properties.ObjectProperty;
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
			final Ordered annotation = metadata.getAnnotation(Ordered.class);
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
	@Override
	public List<ClassPropertyEntry<T>> createProperties(final IScopedSupport propertySupport,
			IObjectProvider<T> object) {

		final List<ClassPropertyEntry<T>> properties = new ArrayList<>();
		for (final AbstractAttributeMetaData<T> attrib : metaData.getAttributes()) {

			final ObjectProperty<Object> property = new ObjectProperty<>(attrib.getName(), propertySupport);
			property.configureTyped(persistent(object, Persisters.attribute(attrib)));

			final boolean readOnly = config.readOnly || attrib.isReadOnly();
			final String message = findText(attrib, Labeled::label, PropertyEntry::descriptionKey);
			final String toolTip = findText(attrib, Labeled::tooltip, PropertyEntry::tooltipKey);
			properties.add(
					new ClassPropertyEntry<>(property, this::createBindingChain, attrib, readOnly, message, toolTip));
		}
		properties.sort(Comparator.comparing(ClassPropertyEntry::index));
		return properties;
	}

	private EndOfChain<?> createBindingChain(AbstractTypedProperty<?> property) {
		EndOfChain<?> chain = property.createBindingChain();
		for (final IGenericModelAdapter adapter : config.adapters) {
			chain = adapter.apply(config.editedClazz, chain);
		}
		return chain;
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

}
