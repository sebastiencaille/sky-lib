package ch.scaille.tcwriter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TCActors {

	/**
	 * List of code|role_simple_call_name|description|human_readable
	 * 
	 * @return
	 */
	String[] value();

}
