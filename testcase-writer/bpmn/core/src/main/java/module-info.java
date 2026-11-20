import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.bpmn.core {
    exports ch.scaille.tcwriter.bpmn.core;
    requires transitive testcase.writer.model;
    requires transitive velocity.engine.core;
    requires org.apache.commons.lang3;
	requires com.google.common;
}