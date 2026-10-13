package ch.scaille.tcwriter.server.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jspecify.annotations.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.regex.Pattern;

public class NullableIdValidator implements ConstraintValidator<Id, JsonNullable<String>> {

	private static final Pattern ID_VALIDATOR = Pattern.compile("^[\\w\\-.]+$");

	@Override
	public boolean isValid(JsonNullable<String> id, ConstraintValidatorContext context) {
		return id.map(strId -> ID_VALIDATOR.matcher(strId).find()).orElse(true);
	}
}