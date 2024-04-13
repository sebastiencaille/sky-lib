package ch.scaille.tcwriter.pilot.selenium;

import java.util.function.Function;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ch.scaille.tcwriter.pilot.Polling;
import ch.scaille.tcwriter.pilot.PollingBuilder;
import ch.scaille.tcwriter.pilot.PollingContext;
import ch.scaille.tcwriter.pilot.factories.PollingResults;

public class SeleniumPollingBuilder extends PollingBuilder<ElementPilot, WebElement> {

	public SeleniumPollingBuilder(ElementPilot elementPilot) {
		super(elementPilot);
	}

	public SeleniumPollingBuilder wrap(PollingBuilder<ElementPilot, WebElement> builder) {
		return (SeleniumPollingBuilder) builder;
	}

	private WebDriver driverFrom(PollingContext<WebElement> ctxt) {
		return ctxt.getGuiPilot().unwrap(SeleniumPilot.class).getDriver();
	}

	public ResultHandler<Boolean> satisfies(Function<WebElement, ExpectedCondition<WebElement>> expectedCondition) {
		return withConfiguration(polling -> polling.withReportText(expectedCondition.toString()))
				.poll(new Polling<>(ctxt -> PollingResults
						.value(expectedCondition.apply(ctxt.getComponent()).apply(driverFrom(ctxt)) != null)));
	}

	public ResultHandler<Boolean> isEnabled() {
		return wrap(withConfiguration(polling -> polling.withReportText("is enabled")))
				.satisfies(ExpectedConditions::elementToBeClickable);
	}

	public ResultHandler<Boolean> click() {
		return withConfiguration(polling -> polling.withReportText("clicked")).poll(new Polling<>(
				ctxt -> ExpectedConditions.elementToBeClickable(ctxt.getComponent()).apply(driverFrom(ctxt)) != null,
				ctxt -> {
					ctxt.getComponent().click();
					return PollingResults.success();
				}));
	}

	public ResultHandler<Boolean> textEquals(String text) {
		return withConfiguration(polling -> polling.withReportText("text equals '" + text + "'"))
				.satisfy(element -> element.getText().equals(text));
	}

}
