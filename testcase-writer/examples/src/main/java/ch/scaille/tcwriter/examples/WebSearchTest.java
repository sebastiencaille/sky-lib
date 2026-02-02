package ch.scaille.tcwriter.examples;

import ch.scaille.tcwriter.examples.websearch.AbstractWebSearchTest;
import ch.scaille.tcwriter.examples.websearch.InternautRole;
import ch.scaille.tcwriter.examples.websearch.dto.MatcherDto;
import ch.scaille.tcwriter.examples.websearch.selectors.EngineSearchSelector;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;
import ch.scaille.tcwriter.services.generators.JavaToDictionary;
import ch.scaille.tcwriter.services.recorder.TestCaseRecorderAspect;
import ch.scaille.testing.testpilot.jupiter.DisabledIfHeadless;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Paths;

@ExtendWith(DisabledIfHeadless.class)
public class WebSearchTest extends AbstractWebSearchTest {

	@Test
	public void testNormalCase() {
		final var dictionary = new JavaToDictionary("searchTests", InternautRole.class).generate();
		dictionary.getMetadata().setDescription("Search on internet dictionary");
		final var daoConfig = DaoConfigs.withFolder(Paths.get("/tmp"));
		daoConfig.modelDao().writeTestDictionary(dictionary);

		TestCaseRecorderAspect.setRecorder(null);
		
		internaut.search(EngineSearchSelector.duckDuckGo("Duck"), MatcherDto.startsWith("Duck "));
	}

}
