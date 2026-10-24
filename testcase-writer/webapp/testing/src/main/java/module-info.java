import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.webapp.tests {
	
	exports ch.scaille.tcwriter.it;
	exports ch.scaille.tcwriter.it.testcontainer;
	
	requires spring.context;
	requires spring.beans;

	requires lib.testing.gui.pilot.selenium;
	requires lib.utils;
	requires testcontainers;
	requires com.github.dockerjava.api;

}