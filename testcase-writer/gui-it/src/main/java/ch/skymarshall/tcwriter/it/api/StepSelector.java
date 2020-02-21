package ch.skymarshall.tcwriter.it.api;

import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JTable;

import org.junit.Assert;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.it.TCGuiPilot;
import ch.skymarshall.tcwriter.swingpilot.GuiPilot;

@TCApi(description = "Step selector", humanReadable = "Step selector", isSelector = true)
public class StepSelector {

	private final Consumer<GuiPilot> applier;

	protected StepSelector(final Consumer<GuiPilot> applier) {
		this.applier = applier;
	}

	public void select(final GuiPilot guiPilot) {
		guiPilot.withSwing(() -> applier.accept(guiPilot));
	}

	protected static JTable getStepsTable(final GuiPilot guiPilot) {
		return guiPilot.getComponent(TCGuiPilot.STEPS_TABLE, JTable.class);
	}

	@TCApi(description = "Step at index", humanReadable = "select the step %s")
	public static StepSelector selectStep(@TCApi(description = "index", humanReadable = "at row") final int ordinal) {
		return new StepSelector(guiPilot -> {
			final int tableIndex = ordinal - 1;
			final JTable stepsTable = getStepsTable(guiPilot);
			Assert.assertTrue("Step does not exist", tableIndex < stepsTable.getRowCount());
			stepsTable.setRowSelectionInterval(tableIndex, tableIndex);
		});

	}

	@TCApi(description = "Append a step to the test", humanReadable = "add a step to the test case")
	public static StepSelector addStep() {
		return new StepSelector(guiPilot -> {
			final JTable stepsTable = getStepsTable(guiPilot);
			final int stepsCount = stepsTable.getRowCount();
			if (stepsCount > 0) {
				stepsTable.setRowSelectionInterval(stepsCount - 1, stepsCount - 1);
			}
			guiPilot.getComponent(TCGuiPilot.ADD_STEP, JButton.class).doClick();
		});

	}

	@TCApi(description = "Selected step", humanReadable = "")
	public static StepSelector currentStep() {
		return new StepSelector(guiPilot -> {
			final JTable stepsTable = getStepsTable(guiPilot);
			Assert.assertTrue("Step must be selected", stepsTable.getSelectedRowCount() > 0);
		});
	}

}
