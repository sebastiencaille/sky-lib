import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.generator.utils {
    exports ch.scaille.generators.util;
    requires transitive lib.utils;
    requires jcommander;
}