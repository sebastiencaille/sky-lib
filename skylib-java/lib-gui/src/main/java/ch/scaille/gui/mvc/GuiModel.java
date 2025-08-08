package ch.scaille.gui.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.converters.IUnaryConverter;
import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;
import ch.scaille.javabeans.properties.AbstractTypedProperty;
import ch.scaille.javabeans.properties.ErrorSet;

public class GuiModel implements IPropertiesOwner {

	public interface ImplicitConvertProvider {
		/**
		 * Creates an implicit converter
		 */
		<T, U> IUnaryConverter<U> create(Class<T> modelClass, AbstractTypedProperty<U> property, String attributeName,
				Class<?> attributeClass);
	}

	public static class ModelConfiguration {

		protected final IPropertiesGroup propertySupport;

		protected ErrorNotifier errorNotifier;

		/**
		 * Allows converting a persisted value to a Property value
		 */
		protected final List<ImplicitConvertProvider> implicitConverters = new ArrayList<>();

		public ModelConfiguration(IPropertiesGroup propertySupport) {
			this.propertySupport = propertySupport;
		}

		public ModelConfiguration with(ErrorNotifier errorNotifier) {
			this.errorNotifier = errorNotifier;
			return this;
		}

		public ModelConfiguration ifNotSet(Supplier<ErrorNotifier> errSupplier) {
			if (this.errorNotifier == null) {
				this.errorNotifier = errSupplier.get();
			}
			return this;
		}

		public ModelConfiguration with(ImplicitConvertProvider factory) {
			implicitConverters.add(factory);
			return this;
		}

		public List<ImplicitConvertProvider> getImplicitConverters() {
			return implicitConverters;
		}

		public ModelConfiguration validate() {
			if (errorNotifier == null) {
				errorNotifier = createErrorProperty("InputError", this);
			}
			return this;
		}

		public IPropertiesGroup getPropertySupport() {
			return propertySupport;
		}

		public ErrorNotifier getErrorNotifier() {
			return errorNotifier;
		}

	}

	public static ModelConfiguration with(final IPropertiesGroup propertySupport, final ErrorNotifier errorProperty) {
		return new ModelConfiguration(propertySupport).with(errorProperty);
	}

	public static ModelConfiguration with(final IPropertiesGroup propertySupport) {
		return new ModelConfiguration(propertySupport);
	}

	public static ModelConfiguration of(final GuiController controller) {
		return new ModelConfiguration(controller.getScopedChangeSupport());
	}

	public static ErrorNotifier createErrorProperty(final String name, final ModelConfiguration config) {
		return new ErrorSet(name, config.getPropertySupport());
	}

	protected final ModelConfiguration configuration;

	public GuiModel(ModelConfiguration configuration) {
		this.configuration = configuration.validate();
	}

	public ModelConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public ErrorNotifier getErrorNotifier() {
		return configuration.getErrorNotifier();
	}

	@Override
	public IPropertiesGroup getPropertySupport() {
		return configuration.propertySupport;
	}

	/**
	 * Creates a property that uses implicit converters. 
	 */
	public <T, U> Consumer<AbstractTypedProperty<U>> implicitConverters(Class<T> modelClass, String attributeName,
			Class<?> attributeClass) {
		return p -> configuration.getImplicitConverters()
				.stream()
				.map(c -> c.create(modelClass, p, attributeName, attributeClass))
				.forEach(p::addImplicitConverter);
	}

	public void activate() {
		configuration.propertySupport.transmitChangesBothWays();
	}

}
