import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.webapp.tests {
	requires spring.context;
	requires spring.beans;
	requires jakarta.annotation;
	requires testcase.writer.core;
	requires testcase.writer.javatc;
	requires testcase.writer.examples;
	requires lib.testing.gui.pilot.selenium;
	requires lib.utils;
}