package ch.scaille.testing.testpilot.selenium;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.bidi.script.ChannelValue;
import org.openqa.selenium.remote.DomMutation;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Script;
import org.openqa.selenium.support.ui.ExpectedCondition;

import ch.scaille.testing.testpilot.ModalDialogDetector;
import ch.scaille.testing.testpilot.ModalDialogDetector.PollingResult;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.helpers.NoExceptionCloseable;

public class SeleniumPilot extends ch.scaille.testing.testpilot.GuiPilot {

	private static final Logger LOGGER = Logs.of(SeleniumPilot.class);
	
	private final RemoteWebDriver driver;
	private final Script remoteScript;
	private final org.openqa.selenium.bidi.module.Script script;
	private final List<Long> domMutationHandlerIds = new ArrayList<>();
	private final List<DomMutation> mutations = new ArrayList<>();
	private Predicate<DomMutation> mutationFilter = null;

	public SeleniumPilot(final RemoteWebDriver driver) {
		this.driver = driver;
		this.script = new org.openqa.selenium.bidi.module.Script(driver);
		this.remoteScript = driver.script();
		installPathFunction();
		domMutationHandlerIds.add(this.remoteScript.addDomMutationHandler(this::mutationHandler));
		this.prelaodScript("js/bidi-text-mutation-listener.js", "channel_name");
	}

	private void mutationHandler(DomMutation mutation) {
		final var elementPath = getElementPath(mutation.getElement());
		if (elementPath.isEmpty()) {
			return;
		}
		LOGGER.info(() -> "Received on %s, %s: %s -> %s".formatted(elementPath,
						mutation.getAttributeName(), mutation.getOldValue(), mutation.getCurrentValue()));
		if (mutationFilter != null && mutationFilter.test(mutation)) {
			synchronized (mutations) {
				mutations.add(mutation);
			}
		}
	}

	/**
	 * Only inject a new script, that will use the existing handler. The code comes
	 * from selenium's code
	 */
	public void prelaodScript(String resourceName, String channelname) {
		try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
			if (stream == null) {
				throw new IllegalStateException("Unable to find helper script");
			}
			final var scriptValue = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

			final List<ChannelValue> arguments;
			if (channelname != null) {
				arguments = List.of(new ChannelValue(channelname));
			} else {
				arguments = List.of();
			}
			this.script.addPreloadScript(scriptValue, arguments);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to read helper script");
		}

	}

	@Override
	public void close() {
		domMutationHandlerIds.forEach(remoteScript::removeDomMutationHandler);
		script.close();
		super.close();
	}

	public WebDriver getDriver() {
		return driver;
	}

	public ElementPilot element(final By locator) {
		return new ElementPilot(this, webDriver -> webDriver.findElement(locator));
	}

	public ElementPilot element(final ExpectedCondition<WebElement> expectedCondition) {
		return new ElementPilot(this, expectedCondition);
	}

	public AlertPilot alert() {
		return new AlertPilot(this);
	}

	/**
	 * Creates a page (method/constructor that takes a SeleniumPilot as parameter)
	 */
	public <C extends PagePilot> C page(Function<SeleniumPilot, C> factory) {
		return factory.apply(this);
	}

	@Override
	protected ModalDialogDetector.Builder createDefaultModalDialogDetector() {
		final var testThread = Thread.currentThread();
		return new ModalDialogDetector.Builder(() -> AlertDetector.listAlerts(this, null), e -> testThread.interrupt());
	}

	/**
	 * This api can be used with try/finally
	 */
	public NoExceptionCloseable expectModalDialog(final Function<AlertPilot, PollingResult> check) {
		final var testThread = Thread.currentThread();
		return expectModalDialog(new ModalDialogDetector.Builder(() -> AlertDetector.listAlerts(this, check),
				e -> testThread.interrupt()));
	}

	protected void installPathFunction() {
		prelaodScript("js/compute-path-function.js", null);
	}

	public String getElementPath(WebElement element) {
		return Objects.requireNonNull(driver.executeScript("return computePath(arguments[0])", element)).toString();
	}

	public void expectMutations(Predicate<DomMutation> filter) {
		synchronized (mutations) {
			mutations.clear();
			mutationFilter = filter;
		}
	}

	public List<DomMutation> getMutations(Predicate<DomMutation> filter) {
		synchronized (mutations) {
			return mutations.stream().filter(filter).toList();
		}
	}

}
