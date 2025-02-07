package ch.scaille.tcwriter.it;

import static ch.scaille.tcwriter.it.api.ParameterSelector.currentSelector;
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
import ch.scaille.tcwriter.it.api.TestContent;
import ch.scaille.tcwriter.it.api.TestSessionRole;
import ch.scaille.tcwriter.it.api.TestWriterRole;
import ch.scaille.tcwriter.pilot.PollingContext;
import ch.scaille.tcwriter.pilot.PollingResult;
import ch.scaille.tcwriter.pilot.factories.PollingResults;
import ch.scaille.tcwriter.pilot.factories.Reporting;

public class LocalTCWriterRole implements TestSessionRole, TestWriterRole {

	private final TCWriterPage tcWriterPage;
	private final TCGuiPilot guiPilot;

	public LocalTCWriterRole(final TCGuiPilot guiPilot) {
		this.guiPilot = guiPilot;
		this.tcWriterPage = guiPilot.page(TCWriterPage::new);
	}


	@Override
	public void doSelectStep(final StepSelector selector) {
		selector.accept(tcWriterPage);
	}

	@Override
	public void doEditStep(final StepSelector selector, final StepEdition edition) {
		selector.accept(tcWriterPage);
		tcWriterPage.actors.select(edition.getActor());
		tcWriterPage.actions.select(edition.getAction());
		tcWriterPage.selectors.select(edition.getSelector());
		tcWriterPage.parameters0.select(edition.getParameter());
	}

	@Override
	public void doUpdateStep(final StepSelector selector, final StepEdition edition) {
		editStep(selector, edition);
		applyStepEdition();
	}

	private void applyStepEdition() {
		tcWriterPage.applyStep.click();
	}

	@Override
	public void doAssertStepContent(final StepSelector selector, final StepEdition edition) {
		selector.accept(tcWriterPage);
		tcWriterPage.actors.assertSelected(edition.getActor());
		tcWriterPage.actions.assertSelected(edition.getAction());
		tcWriterPage.selectors.assertSelected(edition.getSelector());
		tcWriterPage.parameters0.assertSelected(edition.getParameter());
	}

	/**
	 * Checks the text has it would be displayed
	 */
	@Override
	public void doAssertHumanReadable(final StepSelector selector, final String humanReadable) {
		selector.accept(tcWriterPage);

		tcWriterPage.stepsTable
				.fail("checking human readable text: " + humanReadable)
				.ifNot()
				.asserted(pc -> {
					final var component = pc.getComponent();
					final var value = ((StepsTableModel) component.getModel())
							.getHumanReadable(component.getSelectedRow());
					Assertions.assertEquals(humanReadable, value);
				});
	}

	@Override
	public void doUpdateParameter(final ParameterSelector selector, final ParameterValue value) {
		selector.apply(tcWriterPage)
				.fail(Reporting.settingValue("parameter", value))
				.ifNot()
				.appliedCtxt(context -> updateParameterValues(context, value));

		applyStepEdition();

	}

	private PollingResult<JTable, Boolean> updateParameterValues(PollingContext<JTable> context,
			final ParameterValue value) {
		updateValue(context.getComponent(), value.getKeyValue1());
		updateValue(context.getComponent(), value.getKeyValue2());
		updateValue(context.getComponent(), value.getKeyValue3());
		return PollingResults.success();
	}

	@Override
	public void doInjectTest(TestContent testContent) {
		boolean firstStep = true;
		for (var step: testContent.steps()) {
			if (firstStep) {
				updateStep(StepSelector.selectStep(1), step);
				firstStep = false;
			} else if (step.getParameterValue1() != null) {
				editStep(addStep(), step);
				updateParameter(currentSelector(), oneValue(step.getParameterValue1()));
			} else {
				updateStep(addStep(), step);
			} 
		}
	}

	@Override
	public void doAssertTest(TestContent testContent) {
		for (int i = 0; i < testContent.steps().length; i++) {
			assertStepContent(StepSelector.selectStep(i + 1), testContent.steps()[i]);
			assertHumanReadable(currentStep(), testContent.humanReadable()[i]);
		}
	}

	@Override
	public void doManageTest(final MainFrameAction action) {
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
