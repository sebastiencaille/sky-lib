package ch.skymarshall.gui.validation;

import static java.util.stream.Collectors.joining;

import java.util.Set;

import ch.skymarshall.gui.mvc.converters.ConversionException;
import ch.skymarshall.gui.mvc.converters.IConverter;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.util.dao.metadata.MetadataHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ValidationConverter {

	private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private static final Validator validator = factory.getValidator();

	private ValidationConverter() {
	}

	public static <B, T> IConverter<T, T> validator(final Class<B> beanType) {

		return new IConverter<T, T>() {

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

}
