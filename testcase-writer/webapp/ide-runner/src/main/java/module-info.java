import org.jspecify.annotations.NullMarked;

@NullMarked 
module testcase.writer.webapp.ide.runner {
	
	requires testcase.writer.webapp.backend;

    requires spring.boot;
    requires spring.boot.autoconfigure;

}