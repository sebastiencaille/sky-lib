import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.webapp.tests {
	requires jakarta.annotation;
	requires spring.context;
	requires spring.beans;

	requires testcase.writer.examples;
	requires testcase.writer.javatc;

	requires lib.testing.gui.pilot.selenium;
	requires lib.utils;

}