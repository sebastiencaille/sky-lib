package ch.scaille.tcwriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.scaille.util.helpers.Logs;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Extensions implements TestWatcher {

    public static WebDriver driver;

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (driver == null) {
            return;
        }

        try {
            String filePrefix = context.getRequiredTestClass().getName().replace('.', '_') + '_' + context.getRequiredTestMethod().getName();
            Path reportPath = Paths.get("target");
            Files.createDirectories(reportPath);
            Path mainScreenshotPath = reportPath.resolve(filePrefix + "_MAIN_SCREENSHOT.png").toAbsolutePath();
            System.err.println("[[ATTACHMENT|" + mainScreenshotPath + "]]");
            TakesScreenshot screenShot = ((TakesScreenshot) driver);
            Files.write(mainScreenshotPath, screenShot.getScreenshotAs(OutputType.BYTES));
        } catch (IOException e) {
            Logs.of(Extensions.class).log(Level.WARNING, "Unable to take screenshot", e);
        }
    }
}
