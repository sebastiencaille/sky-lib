package ch.scaille.tcwriter.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TCRole {

	String description();

	String humanReadable();

}
