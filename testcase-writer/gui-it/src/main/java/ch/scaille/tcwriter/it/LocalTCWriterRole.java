package ch.scaille.tcwriter.it;

import static ch.scaille.tcwriter.it.api.ParameterSelector.selector;
import static ch.scaille.tcwriter.it.api.ParameterValue.oneValue;
import static ch.scaille.tcwriter.it.api.StepSelector.addStep;
import static ch.scaille.tcwriter.it.api.StepSelector.currentStep;

import javax.swing.JTable;

import org.junit.jupiter.api.Assertions;

import ch.scaille.tcwriter.gui.steps.StepsTableModel;
import ch.scaille.tcwriter.it.api.MainFrameAction;
import ch.scaille.tcwriter.it.api.ParameterSelector;
import ch.scaille.tcwriter.it.api.ParameterValue;
import ch.scaille.tcwriter.it.api.StepEdition;
import ch.scaille.tcwriter.it.api.StepSelector;
import ch.scaille.tcwriter.it.api.TestSessionRole;
import ch.scaille.tcwriter.it.api.TestWriterRole;
import ch.scaille.tcwriter.pilot.ActionPolling;
import ch.scaille.tcwriter.pilot.Factories.PollingResults;
import ch.scaille.tcwriter.pilot.Factories.Reporting;

@SuppressWarnings("java:S5960")
public class LocalTCWriterRole implements TestSessionRole, TestWriterRole {

	private static final String ACTOR_TEST_WRITER = "Test writer";
	private final TCWriterPage tcWriterPage;
	private final TCGuiPilot guiPilot;

	public LocalTCWriterRole(final TCGuiPilot guiPilot) {
		this.guiPilot = guiPilot;
		this.tcWriterPage = guiPilot.page(TCWriterPage::new);
	}

	private StepEdition[] basicTestContents() {
		final var edition1 = new StepEdition();
		edition1.setActor(ACTOR_TEST_WRITER);
		edition1.setAction("Select a step");
		edition1.setSelector("Append a step to the test");

		final var edition2 = new StepEdition();
		edition2.setActor(ACTOR_TEST_WRITER);
		edition2.setAction("Check the Human Readable text");
		edition2.setSelector("Selected step");

		final var edition3 = new StepEdition();
		edition3.setActor(ACTOR_TEST_WRITER);
		edition3.setAction("Select a step");
		edition3.setSelector("Step at index");
		return new StepEdition[] { edition1, edition2, edition3 };
	}

	@Override
	public void selectStep(final StepSelector selector) {
		selector.accept(tcWriterPage);
	}

	@Override
	public void editStep(final StepSelector selector, final StepEdition edition) {
		selector.accept(tcWriterPage);
		tcWriterPage.actors.select(edition.getActor());
		tcWriterPage.actions.select(edition.getAction());
		tcWriterPage.selectors.select(edition.getSelector());
		tcWriterPage.parameters0.select(edition.getParameter());
	}

	@Override
	public void updateStep(final StepSelector selector, final StepEdition edition) {
		editStep(selector, edition);
		applyStepEdition();
	}

	private void applyStepEdition() {
		tcWriterPage.applyStep.click();
	}

	@Override
	public void checkStep(final StepSelector selector, final StepEdition edition) {
		selector.accept(tcWriterPage);
		tcWriterPage.actors.checkSelected(edition.getActor());
		tcWriterPage.actions.checkSelected(edition.getAction());
		tcWriterPage.selectors.checkSelected(edition.getSelector());
		tcWriterPage.parameters0.checkSelected(edition.getParameter());
	}

	/**
	 * Checks the text has it would be displayed
	 */
	@Override
	public void checkHumanReadable(final StepSelector selector, final String humanReadable) {
		selector.accept(tcWriterPage);

		tcWriterPage.stepsTable.wait(tcWriterPage.stepsTable.assertion(pc -> {
			final var component = pc.component;
			final var value = ((StepsTableModel) component.getModel()).getHumanReadable(component.getSelectedRow());
			Assertions.assertEquals(humanReadable, value);
		}).withReportText("checking human readable text: " + humanReadable));
	}

	@Override
	public void updateParameter(final ParameterSelector selector, final ParameterValue value) {
		selector.apply(tcWriterPage).wait(new ActionPolling<JTable, Boolean>(pc -> {
			updateValue(pc.component, value.getKeyValue1());
			updateValue(pc.component, value.getKeyValue2());
			updateValue(pc.component, value.getKeyValue3());
			return PollingResults.success();
		}).withReportText(Reporting.settingValue("parameter", value)));
		applyStepEdition();
	}

	@Override
	public void injectBasicTest() {
		final var basicTestContents = basicTestContents();
		updateStep(StepSelector.selectStep(1), basicTestContents[0]);
		updateStep(addStep(), basicTestContents[1]);
		editStep(addStep(), basicTestContents[2]);
		updateParameter(selector(), oneValue("index:1"));
	}

	@Override
	public void checkBasicTest() {
		final var basicTestContents = basicTestContents();

		checkStep(StepSelector.selectStep(1), basicTestContents[0]);
		checkHumanReadable(currentStep(), "As test writer, I add a step to the test case");

		checkStep(StepSelector.selectStep(2), basicTestContents[1]);
		checkHumanReadable(currentStep(), "As test writer, I check that the human readable text is \"\"");

		checkStep(StepSelector.selectStep(3), basicTestContents[2]);
		checkHumanReadable(currentStep(), "As test writer, I select the step 1");
	}

	@Override
	public void mainFrameAction(final MainFrameAction action) {
		action.execute(tcWriterPage, guiPilot);
	}

	private static void updateValue(final JTable valueTable, final String keyValueStr) {
		if (keyValueStr == null) {
			return;
		}
		final var keyValue = keyValueStr.split(":");
		for (int i = 0; i < valueTable.getRowCount(); i++) {
			if (valueTable.getValueAt(i, 2).equals(keyValue[0])) {
				valueTable.setValueAt(keyValue[1], i, 3);
				return;
			}
		}
		Assertions.fail("No such complex type parameter: " + keyValue[0]);
	}
}
