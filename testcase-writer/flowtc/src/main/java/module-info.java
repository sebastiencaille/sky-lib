import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.flowtc {

    requires testcase.writer.model;

    requires com.google.common;
    requires org.apache.commons.lang3;
    requires velocity.engine.core;
}