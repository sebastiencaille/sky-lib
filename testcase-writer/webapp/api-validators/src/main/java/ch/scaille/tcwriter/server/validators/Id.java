package ch.scaille.tcwriter.server.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = { IdValidator.class })
@Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
	String message() default "The value should be an id";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}