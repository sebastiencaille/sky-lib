import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.model {
	exports ch.scaille.tcwriter.model;
	exports ch.scaille.tcwriter.model.dictionary;
	exports ch.scaille.tcwriter.model.testcase;
	exports ch.scaille.tcwriter.model.testexec;
	
	opens ch.scaille.tcwriter.model.testexec to org.junit.platform.commons;
		
	requires transitive lib.utils;
	requires transitive com.fasterxml.jackson.annotation;
	requires transitive com.google.common;
}
