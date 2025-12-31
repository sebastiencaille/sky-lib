package ch.scaille.testing.testpilot.selenium.jupiter;

import java.util.Optional;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Allows propagating the web driver to the junit extensions
 */
@NullMarked
public class WebDriverExtension implements ParameterResolver {

	@Nullable
	private static RemoteWebDriver currentDriver = null;
	
	public static void setCurrentDriver(RemoteWebDriver currentDriver) {
		WebDriverExtension.currentDriver = currentDriver;
	}
	
	public static class WebDriverConfigurer {

		public Optional<WebDriver> getDriver() {
			return Optional.ofNullable(currentDriver);
		}

		public void setDriver(RemoteWebDriver webDriver) {
			setCurrentDriver(webDriver);
		}
		
		public RemoteWebDriver getOrCreate(Supplier<RemoteWebDriver> webDriverSupplier) {
			if (currentDriver == null) {
				setDriver(webDriverSupplier.get());
			}
			return currentDriver;
		}

	}

	public static Optional<WebDriver> getDriver() {
		return Optional.ofNullable(currentDriver);
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
