package ch.skymarshall.gui.validation;

import static java.util.stream.Collectors.joining;

import java.util.Set;

import ch.skymarshall.gui.mvc.GuiModel.ImplicitConvertProvider;
import ch.skymarshall.gui.mvc.converters.ConversionException;
import ch.skymarshall.gui.mvc.converters.IUnaryConverter;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.gui.mvc.properties.AbstractTypedProperty;
import ch.skymarshall.util.dao.metadata.MetadataHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ValidationBinding {

	private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private static final Validator validator = factory.getValidator();

	private ValidationBinding() {
	}

	public static <B, T> IUnaryConverter<T> validator(final Class<B> beanType) {

		return new IUnaryConverter<T>() {

			private String propertyName;

			@Override
			public void initialize(final AbstractProperty p) {
				propertyName = MetadataHelper.toFirstLetterInLowerCase(p.getName());
			}

			@Override
			public T convertPropertyValueToComponentValue(final T propertyValue) {
				return propertyValue;
			}

			@Override
			public T convertComponentValueToPropertyValue(final T componentValue) throws ConversionException {
				final Set<ConstraintViolation<B>> validation = validator.validateValue(beanType, propertyName,
						componentValue);
				if (!validation.isEmpty()) {
					throw new ConversionException(
							validation.stream().map(ConstraintViolation::getMessage).collect(joining(", ")));
				}
				return componentValue;
			}
		};

	}

	private static class Converter<T, U> implements IUnaryConverter<U> {

		private final Class<T> beanType;
		private final String attributeName;

		public Converter(AbstractTypedProperty<U> prop, Class<T> modelClass, String attributeName, Class<?> attributeClass) {
			this.beanType = modelClass;
			this.attributeName = MetadataHelper.toFirstLetterInLowerCase(attributeName);
		}

		@Override
		public U convertPropertyValueToComponentValue(final U propertyValue) {
			return propertyValue;
		}

		@Override
		public U convertComponentValueToPropertyValue(final U componentValue) throws ConversionException {
			final Set<ConstraintViolation<T>> validation = validator.validateValue(beanType, attributeName,
					componentValue);
			if (!validation.isEmpty()) {
				throw new ConversionException(
						validation.stream().map(ConstraintViolation::getMessage).collect(joining(", ")));
			}
			return componentValue;
		}
	}

	public static ImplicitConvertProvider validator() {
		return Converter::new;
	}

}
