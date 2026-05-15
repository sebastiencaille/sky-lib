import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.gui {
	exports ch.scaille.tcwriter.gui.frame;
	exports ch.scaille.tcwriter.gui.steps;
	
	opens translations;
	
    requires transitive testcase.writer.core;
    requires transitive testcase.writer.javatc;
    requires lib.gui;
    requires lib.javabeans;
    requires lib.utils;
    requires jcommander;
    requires com.google.common;
}