import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.gui.validation {
    exports ch.scaille.gui.validation;
    requires lib.utils;
    requires lib.javabeans;
    requires transitive lib.gui;
    requires transitive jakarta.validation;
    requires transitive jakarta.el;
}