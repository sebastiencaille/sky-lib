import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.flowtc {
    requires testcase.writer.model;
    requires velocity.engine.core;
    requires org.apache.commons.lang3;
	requires com.google.common;
}