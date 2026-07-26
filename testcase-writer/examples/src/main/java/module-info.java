import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.examples {
	
	exports ch.scaille.tcwriter.examples;
	opens userResources.templates;

	requires testcase.writer.core;
	requires testcase.writer.javatc.client;
		
	requires testcase.writer.api;
	requires testcase.writer.gui;
	requires testcase.writer.javatc;
	requires lib.testing.gui.pilot.selenium;
	requires lib.javabeans;
	requires lib.generator.utils;
	requires org.junit.jupiter.api;
	
}