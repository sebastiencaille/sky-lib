package ch.scaille.testing.testpilot.selenium.jupiter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import ch.scaille.util.helpers.Logs;

/**
 * Takes screenshots through the webdriver. Initializing the WebDriverExtension is required
 */
@NullMarked
public class ScreenShotExtensions implements TestWatcher {
	
    @Override
    public void testFailed(ExtensionContext context, @Nullable Throwable cause) {
    	WebDriverExtension.getDriver().ifPresent(driver -> {

            try {
                final var filePrefix = context.getRequiredTestClass().getName().replace('.', '_') + '_' + context.getRequiredTestMethod().getName();
                final var reportPath = Paths.get("target");
                Files.createDirectories(reportPath);
                final var mainScreenshotPath = reportPath.resolve(filePrefix + "_screenshot.png").toAbsolutePath();
                final var screenShotTaker = ((TakesScreenshot) driver);
                Files.write(mainScreenshotPath, screenShotTaker.getScreenshotAs(OutputType.BYTES));
            } catch (IOException e) {
                Logs.of(ScreenShotExtensions.class).log(Level.WARNING, "Unable to take screenshot", e);
            }
        });
    }
}
