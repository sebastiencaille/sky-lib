package ch.scaille.tcwriter.server.validators;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IdValidator implements ConstraintValidator<Id, String> {

	private static final Pattern ID_VALIDATOR = Pattern.compile("^[\\w\\-]+$");

	@Override
	public boolean isValid(String id, ConstraintValidatorContext context) {
		if (id == null) {
			return true;
		}
		return ID_VALIDATOR.matcher(id).find();
	}
}