package ch.scaille.tcwriter.server.validators;

import java.util.regex.Pattern;

import org.openapitools.jackson.nullable.JsonNullable;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullableIdValidator implements ConstraintValidator<Id, JsonNullable<String>> {

	private static final Pattern ID_VALIDATOR = Pattern.compile("^[\\w\\-.]+$");

	@Override
	public boolean isValid(JsonNullable<String> id, ConstraintValidatorContext context) {
		return id.map(strId -> ID_VALIDATOR.matcher(strId).find()).orElse(true);
	}
}