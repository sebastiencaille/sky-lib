package ch.skymarshall.tcwriter.it;

import static org.junit.Assert.assertEquals;

import javax.swing.JButton;
import javax.swing.JTable;

import org.junit.Assert;

import ch.skymarshall.tcwriter.gui.steps.StepsTableModel;

public class LocalTCWriterRole implements TestWriterRole {

	private final TCGuiPilot guiPilot;

	public LocalTCWriterRole(final TCGuiPilot guiPilot) {
		this.guiPilot = guiPilot;
	}

	@Override
	public void selectStep(final StepSelector selector) {
		selector.select(guiPilot);
	}

	@Override
	public void editStep(final StepSelector selector, final StepEdition edition) {
		selector.select(guiPilot);
		guiPilot.withSwing(() -> {
			guiPilot.selectInList("Actors", edition.getActor());
			guiPilot.selectInList("Actions", edition.getAction());
			guiPilot.selectInList("Selectors", edition.getSelector());
			guiPilot.selectInList("Parameters0", edition.getParameter());
		});
	}

	@Override
	public void updateStep(final StepSelector selector, final StepEdition edition) {
		editStep(selector, edition);
		guiPilot.withSwing(() -> {
			applyStepEdition();
		});
	}

	private void applyStepEdition() {
		guiPilot.getComponent("ApplyStep", JButton.class).doClick();
	}

	@Override
	public void checkStep(final StepSelector selector, final StepEdition edition) {
		selector.select(guiPilot);
		guiPilot.withSwing(() -> {
			guiPilot.checkSelectedInList("Actors", edition.getActor());
			guiPilot.checkSelectedInList("Actions", edition.getAction());
			guiPilot.checkSelectedInList("Selectors", edition.getSelector());
			guiPilot.checkSelectedInList("Parameters0", edition.getParameter());
		});
	}

	/**
	 * Checks the text has it would be displayed
	 */
	@Override
	public void checkHumanReadable(final StepSelector selector, final String humanReadable) {
		selector.select(guiPilot);
		guiPilot.withSwing(() -> {
			final JTable stepsTable = guiPilot.getComponent("StepsTable", JTable.class);
			final Object value = ((StepsTableModel) stepsTable.getModel())
					.getHumanReadable(stepsTable.getSelectedRow());
			assertEquals(humanReadable, value.toString());
		});
	}

	@Override
	public void updateParameter(final ParameterSelector selector, final ParameterValue value) {
		guiPilot.withSwing(() -> {
			final JTable valueTable = guiPilot.getComponent(selector.getTableName(), JTable.class);
			updateValue(valueTable, value.getKeyValue1());
			updateValue(valueTable, value.getKeyValue2());
			updateValue(valueTable, value.getKeyValue3());
			applyStepEdition();
		});
	}

	private void updateValue(final JTable valueTable, final String keyValueStr) {
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
