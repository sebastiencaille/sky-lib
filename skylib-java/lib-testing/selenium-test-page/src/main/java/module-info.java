import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.testing.selenium.test.page {
    exports ch.scaille.tcwriter.pilot.selenium;
    opens ch.scaille.tcwriter.pilot.selenium;
    requires transitive lib.testing.gui.pilot.selenium;
    requires undertow.core;
}