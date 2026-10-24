import org.jspecify.annotations.NullMarked;

import ch.scaille.tcwriter.examples.ExampleConfigurationBootstrapService;
import ch.scaille.testing.api.TestBootstrapService;

@NullMarked
module testcase.writer.examples {
	
	exports ch.scaille.tcwriter.examples;
	opens userResources.templates;

	requires testcase.writer.core;
		
	requires testcase.writer.api;
	requires testcase.writer.gui;
	requires testcase.writer.javatc.core;
	requires testcase.writer.javatc.recorder;
	
	requires lib.testing.core;
	requires lib.testing.gui.pilot.selenium;
	requires lib.javabeans;
	requires lib.generator.utils;
	requires org.junit.jupiter.api;

	provides TestBootstrapService with ExampleConfigurationBootstrapService; 
	
}