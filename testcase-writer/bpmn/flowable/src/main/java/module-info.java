import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.bpmn.flowable {
	
	opens ch.scaille.tcwriter.bpmn.flowable to org.junit.platform.commons, velocity.engine.core;
	opens templates.flowable; 
	
    requires testcase.writer.bpmn.core;
    requires org.jspecify;
    requires org.apache.commons.lang3;
}