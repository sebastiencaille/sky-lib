import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.gui.it {
	
	exports ch.scaille.tcwriter.it;
	opens ch.scaille.tcwriter.it to lib.utils;

	requires lib.utils;
	requires lib.testing.gui.pilot.swing; 
	
	requires testcase.writer.api;
	requires testcase.writer.javatc.core;
	requires testcase.writer.core;
	requires testcase.writer.gui;

	requires java.desktop;

	requires org.junit.jupiter.api;

}