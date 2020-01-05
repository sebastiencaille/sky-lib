package ch.skymarshall.tcwriter.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for classes/interfaces methods that are part of the test api.
 * Methods must be in classes / interfaces tagged with TCActor/TCApi/TCData
 *
 * @author scaille
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TCApi {

	String description();

	/**
	 * Human readable text, use %s to inline parameters.<br>
	 * It is possible to use // ... %s ... // to skip a whole block of text if a
	 * parameter is empty.<br>
	 * Hello// %srld// would be formatted as "Hello world" if parameter is "wo", and
	 * "Hello" is parameter is empty<br>
	 * To skip this behavior, use "/\\/" instead of "//"
	 *
	 * @return
	 */
	String humanReadable();

	boolean isSelector() default false;

}
