import ch.scaille.testing.api.TestBootstrapService;
import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.testing.core {

    exports ch.scaille.testing.api;
	
    requires transitive org.jspecify;
    
    requires java.logging;
    
    requires spring.core;
	requires spring.context;
	requires spring.beans;
	requires spring.boot.autoconfigure;
	requires lombok;

    uses TestBootstrapService;

}