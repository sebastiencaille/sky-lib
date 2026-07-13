import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.javatc {
	exports ch.scaille.tcwriter.persistence.testexec;
    exports ch.scaille.tcwriter.javatc.testexec;
    exports ch.scaille.tcwriter.javatc.generators;
    exports ch.scaille.tcwriter.javatc.recorder;
    exports ch.scaille.tcwriter.javatc.visitors;

    requires transitive testcase.writer.core;
    requires transitive testcase.writer.javatc.client;
    
    requires transitive lib.generator.utils;

	requires org.aspectj.weaver;

    requires lib.annotations;
    requires lib.utils;
    
    requires com.google.common;
	requires jcommander;
    
}