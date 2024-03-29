package ch.scaille.tcwriter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface Recorded {

	boolean enabled() default true;
	
	String fsModelConfig() default "";
	
	String dictionary() default "";
	
}
