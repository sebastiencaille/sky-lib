package ch.skymarshall.tcwriter.it.api;

import static ch.skymarshall.tcwriter.pilot.Polling.assertion;

import java.util.function.Consumer;

import org.junit.Assert;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.it.TCGuiPilot;
import ch.skymarshall.tcwriter.pilot.Polling;
import ch.skymarshall.tcwriter.pilot.swing.SwingGuiPilot;
import ch.skymarshall.tcwriter.pilot.swing.SwingButton;
import ch.skymarshall.tcwriter.pilot.swing.SwingTable;

@TCApi(description = "Step selector", humanReadable = "Step selector", isSelector = true)
public class StepSelector {

	private final Consumer<SwingGuiPilot> applier;

	protected StepSelector(final Consumer<SwingGuiPilot> applier) {
		this.applier = applier;
	}

	public void select(final SwingGuiPilot guiPilot) {
		applier.accept(guiPilot);
	}

	protected static SwingTable getStepsTable(final SwingGuiPilot guiPilot) {
		return new SwingTable(guiPilot, TCGuiPilot.STEPS_TABLE);
	}

	@TCApi(description = "Step at index", humanReadable = "select the step %s")
	public static StepSelector selectStep(@TCApi(description = "index", humanReadable = "at row") final int ordinal) {
		return new StepSelector(guiPilot -> {
			final int tableIndex = ordinal - 1;
			getStepsTable(guiPilot).withReport(c -> "step " + ordinal + " exists").waitReadSuccess(
					assertion(t -> Assert.assertTrue("Step does not exist", tableIndex < t.getRowCount())));
			getStepsTable(guiPilot).selectRow(tableIndex);
		});

	}

	@TCApi(description = "Append a step to the test", humanReadable = "add a step to the test case")
	public static StepSelector addStep() {
		return new StepSelector(guiPilot -> {
			final SwingTable stepsTable = getStepsTable(guiPilot);
			stepsTable.withReport(c -> "select last step").waitEditSuccess(Polling.action(t -> {
				final int stepsCount = t.getRowCount();
				if (stepsCount > 0) {
					t.setRowSelectionInterval(stepsCount - 1, stepsCount - 1);
				}
			}));
			new SwingButton(guiPilot, TCGuiPilot.ADD_STEP).click();
		});

	}

	@TCApi(description = "Selected step", humanReadable = "")
	public static StepSelector currentStep() {
		return new StepSelector(guiPilot -> {
			final SwingTable stepsTable = getStepsTable(guiPilot);
			stepsTable.withReport(c -> "a step is selected").waitReadSuccess(
					assertion(c -> Assert.assertTrue("Step must be selected", c.getSelectedRowCount() > 0)));
		});
	}

}
