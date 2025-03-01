package ch.scaille.testing.testpilot.selenium.jupiter;

import java.util.function.Supplier;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.WebDriver;

/**
 * Allows to propagate the web driver to the extensions
 */
public class WebDriverExtension implements ParameterResolver {

	private static WebDriver currentDriver = null;
	
	public static class WebDriverConfigurer {

		public WebDriver getDriver() {
			return currentDriver;
		}

		public void setDriver(WebDriver webDriver) {
			currentDriver = webDriver;
		}
		
		public WebDriver getOrCreate(Supplier<WebDriver> webDriverSupplier) {
			if (currentDriver == null) {
				currentDriver = webDriverSupplier.get();
			}
			return currentDriver;
		}

	}

	public static WebDriver getDriver(ExtensionContext context) {
		return currentDriver;
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		final var parameterType = parameterContext.getParameter().getType();
		return WebDriverConfigurer.class.equals(parameterType);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		return new WebDriverConfigurer();
	}

}
