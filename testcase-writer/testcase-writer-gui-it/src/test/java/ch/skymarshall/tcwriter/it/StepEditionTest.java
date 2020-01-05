package ch.skymarshall.tcwriter.it;

import static ch.skymarshall.tcwriter.it.StepSelector.currentStep;
import static ch.skymarshall.tcwriter.it.StepSelector.selectStep;

import org.junit.Test;

public class StepEditionTest extends AbstractGuiTest {

	@Test
	public void addAndEditSteps() {

		final StepEdition edition1 = new StepEdition();
		edition1.setActor("Test writer");
		edition1.setAction("Select a step");
		edition1.setSelector("Append a step to the test");
		tcWriter.updateStep(selectStep(1), edition1);

		tcWriter.checkHumanReadable(currentStep(), "As test writer, I add a step to the test case");

		final StepEdition edition2 = new StepEdition();
		edition2.setActor("Test writer");
		edition2.setAction("Check the Human Readable text");
		edition2.setSelector("Selected step");
		tcWriter.updateStep(StepSelector.addStep(), edition2);

		tcWriter.checkHumanReadable(currentStep(), "As test writer, I check that the human readable text is \"\"");
	}

}
