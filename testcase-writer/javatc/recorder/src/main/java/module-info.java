import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.javatc.recorder {

    exports ch.scaille.tcwriter.javatc.recorder;
	
    requires transitive testcase.writer.api;

    requires org.jspecify;
	requires org.aspectj.weaver;

}