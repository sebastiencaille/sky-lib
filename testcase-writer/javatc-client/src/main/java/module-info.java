import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.javatc.client {

	exports ch.scaille.tcwriter.javatc.testexec.client;

    requires transitive testcase.writer.api;
	requires transitive testcase.writer.model;
    
	requires transitive org.junit.jupiter.api;

    requires org.jspecify;    	
    requires org.aspectj.weaver;

}