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
				.tryPoll(new Polling<>(ctxt -> PollingResults
						.value(expectedCondition.apply(ctxt.getComponent()).apply(driverFrom(ctxt)) != null)));
	}

	public ResultHandler<Boolean> isEnabledOr() {
		return wrap(withConfiguration(polling -> polling.withReportText("is enabled")))
				.satisfies(ExpectedConditions::elementToBeClickable);
	}
	
	public void isEnabled() {
		isEnabledOr().orFail();
	}

	public ResultHandler<Boolean> clickOr() {
		return withConfiguration(polling -> polling.withReportText("clicked")).tryPoll(new Polling<>(
				ctxt -> ExpectedConditions.elementToBeClickable(ctxt.getComponent()).apply(driverFrom(ctxt)) != null,
				ctxt -> {
					ctxt.getComponent().click();
					return PollingResults.success();
				}));
	}
	
	public void click() {
		clickOr().orFail();
	}

	public ResultHandler<Boolean> textEquals(String text) {
		return withConfiguration(polling -> polling.withReportText("text equals '" + text + "'"))
				.trySatisfy(element -> element.getText().equals(text));
	}

}
