import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.bpmn.camunda {

	opens ch.scaille.tcwriter.bpmn.camunda to org.junit.platform.commons, velocity.engine.core;
	opens templates.camunda; 
	
    requires testcase.writer.bpmn.core;
    requires org.jspecify;
    requires org.apache.commons.lang3;

    requires org.junit.jupiter.api;
}