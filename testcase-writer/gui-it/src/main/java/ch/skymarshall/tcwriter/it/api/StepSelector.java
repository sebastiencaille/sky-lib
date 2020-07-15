package ch.skymarshall.tcwriter.it.api;

import java.util.function.Consumer;

import javax.swing.JTable;

import org.junit.Assert;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.it.TCGuiPilot;
import ch.skymarshall.tcwriter.pilot.EditionPolling;
import ch.skymarshall.tcwriter.pilot.StatePolling;
import ch.skymarshall.tcwriter.pilot.swing.SwingGuiPilot;
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
		return guiPilot.table(TCGuiPilot.STEPS_TABLE);
	}

	@TCApi(description = "Step at index", humanReadable = "select the step %s")
	public static StepSelector selectStep(@TCApi(description = "index", humanReadable = "at row") final int ordinal) {
		return new StepSelector(guiPilot -> {
			final int tableIndex = ordinal - 1;
			getStepsTable(guiPilot).wait(StatePolling
					.<JTable>assertion(t -> Assert.assertTrue("Step does not exist", tableIndex < t.getRowCount()))
					.withReport(c -> "step " + ordinal + " exists"));
			getStepsTable(guiPilot).selectRow(tableIndex);
		});

	}

	@TCApi(description = "Append a step to the test", humanReadable = "add a step to the test case")
	public static StepSelector addStep() {
		return new StepSelector(guiPilot -> {
			getStepsTable(guiPilot).wait(EditionPolling.<JTable>action(t -> {
				final int stepsCount = t.getRowCount();
				if (stepsCount > 0) {
					t.setRowSelectionInterval(stepsCount - 1, stepsCount - 1);
				}
			}).withReport(c -> "select last step"));
			guiPilot.button(TCGuiPilot.ADD_STEP).click();
		});

	}

	@TCApi(description = "Selected step", humanReadable = "")
	public static StepSelector currentStep() {
		return new StepSelector(guiPilot -> getStepsTable(guiPilot).wait(StatePolling
				.<JTable>assertion(c -> Assert.assertTrue("Step must be selected", c.getSelectedRowCount() > 0))
				.withReport(c -> "a step is selected")));
	}

}
