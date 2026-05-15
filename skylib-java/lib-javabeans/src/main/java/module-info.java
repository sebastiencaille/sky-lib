import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.javabeans {
    exports ch.scaille.javabeans.properties;
    exports ch.scaille.javabeans.converters;
    exports ch.scaille.javabeans;
    exports ch.scaille.javabeans.persisters;
    requires transitive java.desktop;
    requires transitive java.logging;
    requires transitive lib.utils;
}