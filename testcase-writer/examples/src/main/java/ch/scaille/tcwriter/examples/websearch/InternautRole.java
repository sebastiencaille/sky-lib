package ch.scaille.tcwriter.examples.websearch;

import ch.scaille.tcwriter.examples.websearch.dto.MatcherDto;
import ch.scaille.tcwriter.examples.websearch.selectors.EngineSearchSelector;
import ch.scaille.testing.testpilot.selenium.PagePilot;
import ch.scaille.testing.testpilot.selenium.SeleniumPilot;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import ch.scaille.tcwriter.annotations.TCAction;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.annotations.TCCheck;
import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.tcwriter.examples.ExampleService;
import ch.scaille.tcwriter.examples.api.interfaces.dto.TestItem;
import ch.scaille.tcwriter.examples.api.interfaces.selectors.BuyingLocationSelector;
import ch.scaille.tcwriter.examples.api.interfaces.selectors.PackageDeliverySelector;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.xml.xpath.XPath;

@TCRole(description = "An internaut", humanReadable = "internaut")
// TODO improve annotation
@TCActors("internaut|internaut")
@RequiredArgsConstructor
public class InternautRole extends Assertions {

	private final SeleniumPilot seleniumPilot;

	@TCApi(description = "Search on internet", humanReadable = "I search %s and click on %s")
	public void search(EngineSearchSelector engineSearch, MatcherDto matcher) {
		final var page = new PagePilot(seleniumPilot);
		seleniumPilot.getDriver().navigate().to(engineSearch.getQueryUrl());
		page.on(By.xpath(engineSearch.getMainPageXPath()))
				.failUnless()
				.applied(mainPage -> {
					System.out.println(mainPage.findElements(By.xpath("//h2/a")));
					mainPage.findElements(By.xpath("//h2/a"))
							.stream().map(s -> { System.out.print(s); return s;})
							.filter(element -> matcher.getMatcher().test(element.getText()))
							.findFirst().ifPresentOrElse(WebElement::click, () -> fail("No link found"));
				});
	}

}