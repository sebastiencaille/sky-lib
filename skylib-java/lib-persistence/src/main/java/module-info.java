import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.persistence {
    exports ch.scaille.util.persistence.handlers;
    exports ch.scaille.util.persistence;
    requires lib.utils;
}