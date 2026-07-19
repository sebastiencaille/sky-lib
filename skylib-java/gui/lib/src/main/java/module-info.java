import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.gui {
    exports ch.scaille.gui.tools;
    exports ch.scaille.gui.mvc;
    exports ch.scaille.gui.mvc.factories;
    exports ch.scaille.gui.model;
    exports ch.scaille.gui.model.views;
    exports ch.scaille.gui.swing;
    exports ch.scaille.gui.swing.jtable;
    exports ch.scaille.gui.swing.model;
    exports ch.scaille.gui.swing.factories;
    exports ch.scaille.gui.swing.bindings;
    exports ch.scaille.gui.swing.tools;
    exports ch.scaille.gui.swing.renderers;

    requires transitive java.desktop;
    requires transitive lib.javabeans;
    requires transitive lib.utils;
    requires lib.annotations;
}