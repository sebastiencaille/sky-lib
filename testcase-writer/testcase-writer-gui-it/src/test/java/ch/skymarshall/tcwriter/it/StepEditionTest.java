package ch.skymarshall.tcwriter.it;

import static ch.skymarshall.tcwriter.it.StepSelector.selectStep;

import org.junit.Test;

public class StepEditionTest extends AbstractGuiTest {

	@Test
	public void addAndEditSteps() {

		final StepEdition edition = new StepEdition();
		edition.setActor("Test writer");
		edition.setAction("Select a step");
		edition.setSelector("Append a step to the test");
		tcWriter.updateStep(selectStep(1), edition);

		tcWriter.checkHumanReadable(StepSelector.currentStep(), "As test writer, I add a step to the test case");
	}

}
