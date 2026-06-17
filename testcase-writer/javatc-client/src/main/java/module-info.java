import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.javatc.client {

	exports ch.scaille.tcwriter.javatc.testexec.recorder;

	exports ch.scaille.tcwriter.javatc.testexec.client;

    requires org.jspecify;
    
    requires transitive testcase.writer.api;
	requires transitive testcase.writer.model;
    
	requires transitive org.junit.jupiter.api;
	
    requires transitive org.aspectj.weaver;

}