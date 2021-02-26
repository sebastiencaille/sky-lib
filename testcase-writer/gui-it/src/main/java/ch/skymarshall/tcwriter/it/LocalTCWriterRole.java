package ch.skymarshall.tcwriter.it;

import static ch.skymarshall.tcwriter.it.api.ParameterSelector.selector;
import static ch.skymarshall.tcwriter.it.api.ParameterValue.oneValue;
import static ch.skymarshall.tcwriter.it.api.StepSelector.addStep;
import static ch.skymarshall.tcwriter.it.api.StepSelector.currentStep;

import javax.swing.JTable;

import org.junit.Assert;

import ch.skymarshall.tcwriter.gui.steps.StepsTableModel;
import ch.skymarshall.tcwriter.it.api.MainFrameAction;
import ch.skymarshall.tcwriter.it.api.ParameterSelector;
import ch.skymarshall.tcwriter.it.api.ParameterValue;
import ch.skymarshall.tcwriter.it.api.StepEdition;
import ch.skymarshall.tcwriter.it.api.StepSelector;
import ch.skymarshall.tcwriter.it.api.TestSessionRole;
import ch.skymarshall.tcwriter.it.api.TestWriterRole;
import ch.skymarshall.tcwriter.pilot.EditionPolling;
import ch.skymarshall.tcwriter.pilot.PollingResult;
import ch.skymarshall.tcwriter.pilot.swing.JTablePilot;

public class LocalTCWriterRole implements TestSessionRole, TestWriterRole {

	private static final String ACTOR_TEST_WRITER = "Test writer";
	private final TCGuiPilot guiPilot;

	public LocalTCWriterRole(final TCGuiPilot guiPilot) {
		this.guiPilot = guiPilot;
	}

	private StepEdition[] basicTestContents() {
		final StepEdition edition1 = new StepEdition();
		edition1.setActor(ACTOR_TEST_WRITER);
		edition1.setAction("Select a step");
		edition1.setSelector("Append a step to the test");

		final StepEdition edition2 = new StepEdition();
		edition2.setActor(ACTOR_TEST_WRITER);
		edition2.setAction("Check the Human Readable text");
		edition2.setSelector("Selected step");

		final StepEdition edition3 = new StepEdition();
		edition3.setActor(ACTOR_TEST_WRITER);
		edition3.setAction("Select a step");
		edition3.setSelector("Step at index");
		return new StepEdition[] { edition1, edition2, edition3 };
	}

	@Override
	public void selectStep(final StepSelector selector) {
		selector.select(guiPilot);
	}

	@Override
	public void editStep(final StepSelector selector, final StepEdition edition) {
		selector.select(guiPilot);
		guiPilot.list("Actors").select(edition.getActor());
		guiPilot.list("Actions").select(edition.getAction());
		guiPilot.list("Selectors").select(edition.getSelector());
		guiPilot.list("Parameters0").select(edition.getParameter());
	}

	@Override
	public void updateStep(final StepSelector selector, final StepEdition edition) {
		editStep(selector, edition);
		applyStepEdition();
	}

	private void applyStepEdition() {
		guiPilot.button("ApplyStep").click();
	}

	@Override
	public void checkStep(final StepSelector selector, final StepEdition edition) {
		selector.select(guiPilot);
		guiPilot.list("Actors").checkSelected(edition.getActor());
		guiPilot.list("Actions").checkSelected(edition.getAction());
		guiPilot.list("Selectors").checkSelected(edition.getSelector());
		guiPilot.list("Parameters0").checkSelected(edition.getParameter());
	}

	/**
	 * Checks the text has it would be displayed
	 */
	@Override
	public void checkHumanReadable(final StepSelector selector, final String humanReadable) {
		selector.select(guiPilot);
		final JTablePilot stepsTable = guiPilot.table("StepsTable");
		stepsTable.wait(stepsTable.assertion(t -> {
			final Object value = ((StepsTableModel) t.getModel()).getHumanReadable(t.getSelectedRow());
			Assert.assertEquals(humanReadable, value.toString());
		}).withReport(c -> "Check human readable text: " + humanReadable));
	}

	@Override
	public void updateParameter(final ParameterSelector selector, final ParameterValue value) {
		guiPilot.table(selector.getTableName()).wait(new EditionPolling<JTable, Boolean>(t -> {
			updateValue(t, value.getKeyValue1());
			updateValue(t, value.getKeyValue2());
			updateValue(t, value.getKeyValue3());
			return PollingResult.success();
		}).withReport(c -> "Set parameter values:" + value), PollingResult.assertFail("Setting " + value));
		applyStepEdition();
	}

	@Override
	public void injectBasicTest() {
		final StepEdition[] basicTestContents = basicTestContents();
		updateStep(StepSelector.selectStep(1), basicTestContents[0]);
		updateStep(addStep(), basicTestContents[1]);
		editStep(addStep(), basicTestContents[2]);
		updateParameter(selector(), oneValue("index:1"));
	}

	@Override
	public void checkBasicTest() {
		final StepEdition[] basicTestContents = basicTestContents();

		checkStep(StepSelector.selectStep(1), basicTestContents[0]);
		checkHumanReadable(currentStep(), "As test writer, I add a step to the test case");

		checkStep(StepSelector.selectStep(2), basicTestContents[1]);
		checkHumanReadable(currentStep(), "As test writer, I check that the human readable text is \"\"");

		checkStep(StepSelector.selectStep(3), basicTestContents[2]);
		checkHumanReadable(currentStep(), "As test writer, I select the step 1");
	}

	@Override
	public void mainFrameAction(final MainFrameAction action) {
		action.execute(guiPilot);
	}

	private static void updateValue(final JTable valueTable, final String keyValueStr) {
		if (keyValueStr == null) {
			return;
		}
		final String[] keyValue = keyValueStr.split(":");
		for (int i = 0; i < valueTable.getRowCount(); i++) {
			if (valueTable.getValueAt(i, 2).equals(keyValue[0])) {
				valueTable.setValueAt(keyValue[1], i, 3);
				return;
			}
		}
		Assert.fail("No such complex type parameter: " + keyValue[0]);
	}
}
