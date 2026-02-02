package ch.scaille.tcwriter.examples.websearch;

import ch.scaille.tcwriter.examples.websearch.dto.MatcherDto;
import ch.scaille.tcwriter.examples.websearch.selectors.EngineSearchSelector;
import ch.scaille.testing.testpilot.selenium.PagePilot;
import ch.scaille.testing.testpilot.selenium.SeleniumPilot;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.annotations.TCRole;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@TCRole(description = "An internaut", humanReadable = "internaut")
// TODO improve annotation
@RequiredArgsConstructor
public class InternautRole extends Assertions {

	private final SeleniumPilot seleniumPilot;

	@TCApi(description = "Search on internet", humanReadable = "I search %s and click on %s")
	public void search(EngineSearchSelector engineSearch, MatcherDto matcher) {
		final var page = new PagePilot(seleniumPilot);
		seleniumPilot.getDriver().navigate().to(engineSearch.queryUrl());
		page.on(By.xpath(engineSearch.mainPageXPath()))
				.failUnless()
				.applied(mainPage -> 
					mainPage.findElements(By.xpath("//h2/a")).stream()
							.filter(element -> matcher.matcher().test(element.getText()))
							.findFirst()
                            .ifPresentOrElse(WebElement::click, () -> fail("No link found")));
        Assertions.assertFalse(page.on(By.xpath(engineSearch.mainPageXPath()))
				.withConfig(c -> c.timeout(Duration.of(3, ChronoUnit.SECONDS)))
				.evaluateThat()
				.present());
	}

}