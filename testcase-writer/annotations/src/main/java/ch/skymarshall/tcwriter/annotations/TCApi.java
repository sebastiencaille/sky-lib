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

	String humanReadable();

	boolean isSelector() default false;

}
