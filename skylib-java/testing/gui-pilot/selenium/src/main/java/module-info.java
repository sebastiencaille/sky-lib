import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.testing.gui.pilot.selenium {
    exports ch.scaille.testing.testpilot.selenium;
    exports ch.scaille.testing.testpilot.selenium.jupiter;
    opens ch.scaille.testing.testpilot.selenium;
    opens js;

    requires transitive lib.gui.pilot.core;
    requires transitive org.opentest4j;
    requires transitive org.seleniumhq.selenium.api;
    requires transitive org.seleniumhq.selenium.firefox_driver;
    requires transitive org.seleniumhq.selenium.remote_driver;
    requires transitive org.seleniumhq.selenium.chrome_driver;
    requires transitive org.seleniumhq.selenium.support;
	
}