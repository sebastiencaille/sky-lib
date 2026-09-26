import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.javatc.core {

	exports ch.scaille.tcwriter.persistence.testexec;
    exports ch.scaille.tcwriter.javatc.testexec;
    exports ch.scaille.tcwriter.javatc.generators;
    exports ch.scaille.tcwriter.javatc.visitors;
    exports ch.scaille.tcwriter.javatc.recorderimpl;

    opens templates;

    requires transitive testcase.writer.api;
    requires transitive testcase.writer.model;
    requires transitive testcase.writer.core;
    requires transitive testcase.writer.javatc.recorder;

    requires transitive lib.generator.utils;

    requires lib.annotations;
    requires lib.utils;
    
    requires com.google.common;
	requires jcommander;

    requires org.aspectj.weaver;
}