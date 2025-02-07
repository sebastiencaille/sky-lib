package ch.scaille.tcwriter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for classes/interfaces methods that are part of the test api.
 * Methods must be in classes / interfaces tagged with TCActor/TCApi/TCData
 *
 * @author scaille
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER })
@Inherited
public @interface TCApi {

	String description();

	/**
	 * Human-readable text, use %s to inline parameters.<br>
	 * It is possible to use // ... %s ... // to skip a whole block of text if a
	 * parameter is empty.<br>
	 * Eg, applying "wo" on Hello// %srld// would produce "Hello world". Applying ""
	 * would produce "Hello"<br>
	 * To skip this behavior, use "/\\/" instead of "//"
	 * <br>
	 * Also, using | would split a text in two. Eg, applying "Hello|world" on "%s
	 * %s" would produce "Hello world"
	 */
	String humanReadable();

	boolean isSelector() default false;

}
