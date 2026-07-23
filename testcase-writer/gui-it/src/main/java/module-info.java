import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.gui.it {
	
	opens userResources.templates;
	
	requires lib.utils;
	requires lib.testing.gui.pilot.swing;
	requires testcase.writer.javatc;
	requires testcase.writer.core;
	requires testcase.writer.gui;
	requires java.desktop;
	requires org.junit.jupiter.api;
	requires testcase.writer.api;
	
}