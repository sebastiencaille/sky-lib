package ch.scaille.tcwriter.server.validators;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jspecify.annotations.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;

public class IdValidator implements ConstraintValidator<Id, String> {

	static final Pattern ID_VALIDATOR = Pattern.compile("^[\\w\\-.]+$");

	@Override
	public boolean isValid(@Nullable String id, ConstraintValidatorContext context) {
		return id == null || ID_VALIDATOR.matcher(id).find();
	}
}