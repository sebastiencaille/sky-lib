package ch.scaille.gui.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.converters.IUnaryConverter;
import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;
import ch.scaille.javabeans.properties.AbstractTypedProperty;
import ch.scaille.javabeans.properties.ErrorSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Getter
@NullMarked
public class GuiModel implements IPropertiesOwner {

	public interface ImplicitConvertProvider {
		/**
		 * Creates an implicit converter
		 */
		<T, U> IUnaryConverter<U> create(Class<T> modelClass, AbstractTypedProperty<U> property, String attributeName,
				Class<?> attributeClass);
	}


	@Builder
	@AllArgsConstructor
    public static class ModelConfiguration {

		public static class ModelConfigurationBuilder {
			public ModelConfigurationBuilder implicitConverters(List<ImplicitConvertProvider> implicitConverters) {
				this.implicitConverters = new ArrayList<>(implicitConverters);
				return this;
			}
		}

		@Getter
		protected final IPropertiesGroup propertySupport;

		protected @Nullable ErrorNotifier errorNotifier;

		protected @Nullable Function<ModelConfiguration, ErrorNotifier> ifNotSet;
		/**
		 * Allows converting a persisted value to a Property value
		 */
		@Getter
		protected final List<ImplicitConvertProvider> implicitConverters;

		public ErrorNotifier getErrorNotifier() {
			if (errorNotifier == null && ifNotSet != null) {
				errorNotifier = ifNotSet.apply(this);
			}
			if (errorNotifier == null) {
				errorNotifier = createErrorProperty("Error", this);
			}
			return errorNotifier;
		}

	}

	public static ModelConfiguration.ModelConfigurationBuilder of(final GuiController controller) {
		return ModelConfiguration.builder().propertySupport(controller.getScopedChangeSupport());
	}

	public static ErrorNotifier createErrorProperty(final String name, final ModelConfiguration config) {
		return new ErrorSet(name, config.getPropertySupport());
	}

	protected final ModelConfiguration configuration;

	public GuiModel(ModelConfiguration.ModelConfigurationBuilder configuration) {
		this.configuration = configuration.build();
	}

    @Override
	public ErrorNotifier getErrorNotifier() {
		return Objects.requireNonNull(configuration.getErrorNotifier());
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
