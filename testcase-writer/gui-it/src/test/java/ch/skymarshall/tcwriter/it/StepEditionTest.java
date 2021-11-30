package ch.skymarshall.tcwriter.it;

import static ch.skymarshall.tcwriter.it.api.ParameterSelector.selector;
import static ch.skymarshall.tcwriter.it.api.ParameterValue.oneValue;
import static ch.skymarshall.tcwriter.it.api.StepSelector.addStep;
import static ch.skymarshall.tcwriter.it.api.StepSelector.currentStep;
import static ch.skymarshall.tcwriter.it.api.StepSelector.selectStep;

import org.junit.jupiter.api.Test;

import ch.skymarshall.tcwriter.it.api.StepEdition;
import ch.skymarshall.tcwriter.jupiter.DisableIfHeadless;

@DisableIfHeadless
class StepEditionTest extends AbstractGuiTest {

	@Test
	void addAndEditSteps() {

		final StepEdition edition1 = new StepEdition();
		edition1.setActor("Test writer");
		edition1.setAction("Select a step");
		edition1.setSelector("Append a step to the test");
		tcWriter.updateStep(selectStep(1), edition1);

		final StepEdition edition2 = new StepEdition();
		edition2.setActor("Test writer");
		edition2.setAction("Check the Human Readable text");
		edition2.setSelector("Selected step");
		tcWriter.updateStep(addStep(), edition2);

		final StepEdition edition3 = new StepEdition();
		edition3.setActor("Test writer");
		edition3.setAction("Select a step");
		edition3.setSelector("Step at index");
		tcWriter.editStep(addStep(), edition3);
		tcWriter.updateParameter(selector(), oneValue("index:1"));

		tcWriter.checkStep(selectStep(1), edition1);
		tcWriter.checkHumanReadable(currentStep(), "As test writer, I add a step to the test case");

		tcWriter.checkStep(selectStep(2), edition2);
		tcWriter.checkHumanReadable(currentStep(), "As test writer, I check that the human readable text is \"\"");

		tcWriter.checkStep(selectStep(3), edition3);
		tcWriter.checkHumanReadable(currentStep(), "As test writer, I select the step 1");

	}

}
