import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.testing.gui.pilot.swing {
	
	exports ch.scaille.testing.testpilot.swing;
	
    requires lib.utils;
    requires transitive lib.gui.pilot.core;
    requires transitive java.desktop;
}