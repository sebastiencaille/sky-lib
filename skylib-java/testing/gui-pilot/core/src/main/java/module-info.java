import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.gui.pilot.core {
    exports ch.scaille.testing.testpilot;
    exports ch.scaille.testing.testpilot.factories;
    exports ch.scaille.testing.testpilot.jupiter;
    opens ch.scaille.testing.testpilot;

    requires transitive org.junit.jupiter.api;
    requires transitive lib.utils;

    requires java.desktop;
}