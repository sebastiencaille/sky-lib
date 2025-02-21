package ch.scaille.gui.tools;

import static ch.scaille.javabeans.properties.Configuration.persistent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import ch.scaille.annotations.Labeled;
import ch.scaille.annotations.Ordered;
import ch.scaille.javabeans.IChainBuilder;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.persisters.IPersisterFactory.IObjectProvider;
import ch.scaille.javabeans.persisters.Persisters;
import ch.scaille.javabeans.properties.AbstractTypedProperty;
import ch.scaille.javabeans.properties.ErrorSet;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.util.dao.metadata.AbstractAttributeMetaData;
import ch.scaille.util.dao.metadata.DataObjectMetaData;
import ch.scaille.util.dao.metadata.IAttributeMetaData;

/**
 * Generic editor based on Class introspection
 * @param <T>
 */
public class GenericEditorClassModel<T> implements IGenericEditorModel<T> {

	public interface IClassPropertyEntry<T> extends IPropertyEntry<T> {
		int index();
	}
	
	public static class ClassPropertyEntry<T, U> extends PropertyEntry<T, U> implements IClassPropertyEntry<T> {

		private final AbstractAttributeMetaData<T, U> metadata;

		public ClassPropertyEntry(final AbstractTypedProperty<U> property,
				final Function<AbstractTypedProperty<U>, IChainBuilder<U, Object>> endOfChainProvider,
				final AbstractAttributeMetaData<T, U> metadata, final boolean readOnly, final String label,
				final String tooltip) {
			super(metadata.getClassType(), property, endOfChainProvider, readOnly, label, tooltip);
			this.metadata = metadata;
		}

		@Override

		public int index() {
			return metadata.getAnnotation(Ordered.class).map(Ordered::order).orElse(Integer.MAX_VALUE / 2);
		}
	}

	public static class Builder<T> {
		private final Class<T> editedClazz;
		private ResourceBundle bundle = null;
		private boolean readOnly = false;
		private IGenericModelAdapter<T>[] adapters = new IGenericModelAdapter[0];
		private IPropertiesGroup propertySupport;
		private ErrorSet errorSet;

		public Builder(final Class<T> clazz) {
			this.editedClazz = clazz;
		}

		/**
		 * Sets the message / tooltips bundle
		 */
		public Builder<T> setBundle(final ResourceBundle bundle) {
			this.bundle = bundle;
			return this;
		}

		public Builder<T> setReadOnly(final boolean readOnly) {
			this.readOnly = readOnly;
			return this;
		}

		/**
		 * Adds the adapters, to tune the property bindings 
		 */
		public Builder<T> addAdapters(final IGenericModelAdapter<T>... adapters) {
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
	 * Creates the properties by introspecting the displayed class
	 */
	@Override
	public List<IPropertyEntry<T>> createProperties(IObjectProvider<T> object) {

		final var properties = new ArrayList<IClassPropertyEntry<T>>();
		for (var attrib : metaData.getAttributes()) {
			attrib.onTypedMetaDataC(t -> createProperty(object, properties, t));
		}
		properties.sort(Comparator.comparing(IClassPropertyEntry::index));
		return properties.stream().map(p -> (IPropertyEntry<T>)p).collect(Collectors.toList());
	}

	private <V> void createProperty(IObjectProvider<T> object, final ArrayList<IClassPropertyEntry<T>> properties,
			AbstractAttributeMetaData<T, V> typedAttribute ) {
		final var property = new ObjectProperty<V>(typedAttribute.getName(), config.propertySupport);
		property.configureTyped(persistent(object, Persisters.persister(typedAttribute)));

		final var readOnly = config.readOnly || typedAttribute.isReadOnly();
		final var message = findText(typedAttribute, Labeled::label, PropertyEntry::descriptionKey);
		final var toolTip = findText(typedAttribute, Labeled::tooltip, PropertyEntry::tooltipKey);
		properties.add(
				new ClassPropertyEntry<>(property, this::createBindingChain, typedAttribute, readOnly, message, toolTip));
	}

	private <V> IChainBuilder<V, Object> createBindingChain(AbstractTypedProperty<V> property) {
		IChainBuilder<V, Object> chain = property.createBindingChain();
		for (final IGenericModelAdapter<T> adapter : config.adapters) {
			chain = adapter.apply(config.editedClazz, chain);
		}
		return chain;
	}

	private String findText(final IAttributeMetaData<T> attrib, final Function<Labeled, String> fromLabel,
			final UnaryOperator<String> nameToKey) {
		var value = attrib.getAnnotation(Labeled.class).map(fromLabel).orElse("");
		if (value.isEmpty() && config.bundle != null) {
			value = config.bundle.getString(nameToKey.apply(attrib.getName()));
		}
		return value;
	}

}
