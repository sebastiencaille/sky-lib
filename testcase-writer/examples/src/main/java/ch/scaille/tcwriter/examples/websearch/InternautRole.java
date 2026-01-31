package ch.scaille.tcwriter.examples.websearch;

import java.util.Objects;

import ch.scaille.tcwriter.examples.websearch.dto.MatcherDto;
import ch.scaille.tcwriter.examples.websearch.selectors.EngineSearchSelector;
import ch.scaille.testing.testpilot.selenium.PagePilot;
import ch.scaille.testing.testpilot.selenium.SeleniumPilot;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.annotations.TCRole;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@TCRole(description = "An internaut", humanReadable = "internaut")
// TODO improve annotation
@TCActors("internaut|internaut")
@RequiredArgsConstructor
public class InternautRole extends Assertions {

	private final SeleniumPilot seleniumPilot;

	@TCApi(description = "Search on internet", humanReadable = "I search %s and click on %s")
	public void search(EngineSearchSelector engineSearch, MatcherDto matcher) throws InterruptedException {
		final var page = new PagePilot(seleniumPilot);
		seleniumPilot.getDriver().navigate().to(engineSearch.getQueryUrl());
		page.on(By.xpath(engineSearch.getMainPageXPath()))
				.failUnless()
				.applied(mainPage -> 
					mainPage.findElements(By.xpath("//h2/a")).stream()
							.filter(element -> matcher.getMatcher().test(element.getText()))
							.findFirst()
                            .ifPresentOrElse(WebElement::click, () -> fail("No link found")));
        Thread.sleep(3000);
	}

}