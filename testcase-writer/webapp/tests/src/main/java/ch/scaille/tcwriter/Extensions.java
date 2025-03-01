package ch.scaille.tcwriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.platform.engine.support.store.NamespacedHierarchicalStore;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import ch.scaille.util.helpers.Logs;

public class Extensions implements TestWatcher {

    public static WebDriver driver;

    @Override
    public void testSuccessful(ExtensionContext context) {
    	// TODO Auto-generated method stub
    	TestWatcher.super.testSuccessful(context);
    }
    
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
    	context.getStore(Namespace.create("selenium")).get("webDriver", WebDriver.class);
        if (driver == null) {
            return;
        }

        try {
            final var filePrefix = context.getRequiredTestClass().getName().replace('.', '_') + '_' + context.getRequiredTestMethod().getName();
            final var  reportPath = Paths.get("target");
            Files.createDirectories(reportPath);
            final var  mainScreenshotPath = reportPath.resolve(filePrefix + "_screenshot.png").toAbsolutePath();
            final var screenShotTaker = ((TakesScreenshot) driver);
            Files.write(mainScreenshotPath, screenShotTaker.getScreenshotAs(OutputType.BYTES));
        } catch (IOException e) {
            Logs.of(Extensions.class).log(Level.WARNING, "Unable to take screenshot", e);
        }
    }
}
