package ch.scaille.tcwriter.examples;

import ch.scaille.tcwriter.examples.websearch.InternautRole;
import ch.scaille.tcwriter.examples.websearch.dto.MatcherDto;
import ch.scaille.tcwriter.examples.websearch.selectors.EngineSearchSelector;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;
import ch.scaille.tcwriter.services.generators.JavaToDictionary;
import ch.scaille.tcwriter.services.recorder.TestCaseRecorderAspect;
import ch.scaille.testing.testpilot.jupiter.DisabledIfHeadless;
import ch.scaille.testing.testpilot.selenium.SeleniumPilot;
import ch.scaille.testing.testpilot.selenium.WebDriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.nio.file.Paths;

@ExtendWith(DisabledIfHeadless.class)
public class WebSearchTest {

	private final RemoteWebDriver webDriver = new WebDriverFactory.FirefoxDriverFactory()
			.build();

    private final InternautRole internaut = new InternautRole(new SeleniumPilot((webDriver)));
    
	@AfterEach
	public void tearDown() {
		webDriver.quit();
	}

	@Test
	public void testNormalCase() throws InterruptedException {
		final var dictionary = new JavaToDictionary("searchTests", InternautRole.class).generate();
		dictionary.getMetadata().setDescription("Search on internet dictionary");
		final var daoConfig = DaoConfigs.withFolder(Paths.get("/tmp"));
		daoConfig.modelDao().writeTestDictionary(dictionary);

		TestCaseRecorderAspect.setRecorder(null);
		
		internaut.search(EngineSearchSelector.duckDuckGo("Flowable"), MatcherDto.startsWith("Flowable"));
		internaut.search(EngineSearchSelector.bing("ELCA"), MatcherDto.contains("ELCA"));
	}

}
