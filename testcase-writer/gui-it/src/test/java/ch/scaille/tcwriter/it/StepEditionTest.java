package ch.scaille.tcwriter.it;

import static ch.scaille.tcwriter.it.api.ParameterSelector.selector;
import static ch.scaille.tcwriter.it.api.ParameterValue.oneValue;
import static ch.scaille.tcwriter.it.api.StepSelector.addStep;
import static ch.scaille.tcwriter.it.api.StepSelector.currentStep;
import static ch.scaille.tcwriter.it.api.StepSelector.selectStep;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.scaille.tcwriter.it.api.StepEdition;
import ch.scaille.tcwriter.jupiter.DisabledIfHeadless;

@ExtendWith(DisabledIfHeadless.class)
class StepEditionTest extends AbstractGuiTest {

	@Test
	void addAndEditSteps() {

		final var edition1 = new StepEdition();
		edition1.setActor("Test writer");
		edition1.setAction("Select a step");
		edition1.setSelector("Append a step to the test");
		tcWriter.updateStep(selectStep(1), edition1);

		final var edition2 = new StepEdition();
		edition2.setActor("Test writer");
		edition2.setAction("Verify the Human Readable text");
		edition2.setSelector("Selected step");
		tcWriter.updateStep(addStep(), edition2);

		final var edition3 = new StepEdition();
		edition3.setActor("Test writer");
		edition3.setAction("Select a step");
		edition3.setSelector("Step at index");
		tcWriter.editStep(addStep(), edition3);
		tcWriter.updateParameter(selector(), oneValue("index:1"));

		tcWriter.assertStepContent(selectStep(1), edition1);
		tcWriter.assertHumanReadable(currentStep(), "As test writer, I add a step to the test case");

		tcWriter.assertStepContent(selectStep(2), edition2);
		tcWriter.assertHumanReadable(currentStep(), "As test writer, I verify that the human readable text is \"\"");

		tcWriter.assertStepContent(selectStep(3), edition3);
		tcWriter.assertHumanReadable(currentStep(), "As test writer, I select the step 1");

	}

}
