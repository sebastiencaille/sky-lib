import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.generator.utils {
    
	exports ch.scaille.generators.util;
    opens ch.scaille.generators.util to jcommander;
    
    requires transitive lib.utils;
    requires jcommander;
}