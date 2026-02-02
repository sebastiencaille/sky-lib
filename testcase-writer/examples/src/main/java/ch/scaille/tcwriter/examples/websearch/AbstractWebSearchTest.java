package ch.scaille.tcwriter.examples.websearch;

import ch.scaille.tcwriter.annotations.TCActor;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.testing.testpilot.selenium.SeleniumPilot;
import ch.scaille.testing.testpilot.selenium.WebDriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.remote.RemoteWebDriver;

@TCActors(@TCActor(variable = "internaut", humanReadable = "an internaut", description = "An internaut", role=InternautRole.class))
public class AbstractWebSearchTest {

    private final RemoteWebDriver webDriver = new WebDriverFactory.FirefoxDriverFactory()
            .build();

    protected final InternautRole internaut = new InternautRole(new SeleniumPilot((webDriver)));

    @AfterEach
    public void tearDown() {
        webDriver.quit();
    }


}
