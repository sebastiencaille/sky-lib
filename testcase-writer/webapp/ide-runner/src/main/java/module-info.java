import org.jspecify.annotations.NullMarked;

@NullMarked 
module testcase.writer.webapp.ide.runner {

    opens javatc_resources;

    requires testcase.writer.webapp.backend;
	requires testcase.writer.webapp.api.v0_.backend;

    requires spring.boot;
    requires spring.boot.autoconfigure;

}