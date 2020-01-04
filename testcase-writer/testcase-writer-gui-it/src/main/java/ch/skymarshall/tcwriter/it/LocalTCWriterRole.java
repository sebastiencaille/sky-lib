package ch.skymarshall.tcwriter.it;

import static org.junit.Assert.assertEquals;

import javax.swing.JButton;
import javax.swing.JTable;

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
	public void updateStep(final StepSelector selector, final StepEdition edition) {
		selector.select(guiPilot);
		guiPilot.withSwing(() -> {
			guiPilot.selectInList("Actors", edition.getActor());
			guiPilot.selectInList("Actions", edition.getAction());
			guiPilot.selectInList("Selectors", edition.getSelector());
			guiPilot.selectInList("Parameters0", edition.getParameter());
			guiPilot.getComponent("ApplyStep", JButton.class).doClick();
		});
	}

	@Override
	public void checkHumanReadable(final StepSelector selector, final String humanReadable) {
		selector.select(guiPilot);
		guiPilot.withSwing(() -> {
			final JTable stepsTable = guiPilot.getComponent("StepsTable", JTable.class);
			final Object value = stepsTable.getModel().getValueAt(stepsTable.getSelectedRow(),
					StepsTableModel.Column.ACTOR.ordinal());
			assertEquals(humanReadable, value.toString());
		});
	}

}
