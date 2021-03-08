package ch.skymarshall.tcwriter.it.api;

import java.util.function.Consumer;

import javax.swing.JTable;

import org.junit.jupiter.api.Assertions;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.it.TCGuiPilot;
import ch.skymarshall.tcwriter.pilot.EditionPolling;
import ch.skymarshall.tcwriter.pilot.StatePolling;
import ch.skymarshall.tcwriter.pilot.swing.JTablePilot;
import ch.skymarshall.tcwriter.pilot.swing.SwingPilot;

@TCApi(description = "Step selector", humanReadable = "Step selector", isSelector = true)
public class StepSelector {

	private final Consumer<SwingPilot> applier;

	protected StepSelector(final Consumer<SwingPilot> applier) {
		this.applier = applier;
	}

	public void select(final SwingPilot guiPilot) {
		applier.accept(guiPilot);
	}

	protected static JTablePilot getStepsTable(final SwingPilot guiPilot) {
		return guiPilot.table(TCGuiPilot.STEPS_TABLE);
	}

	@TCApi(description = "Step at index", humanReadable = "select the step %s")
	public static StepSelector selectStep(@TCApi(description = "index", humanReadable = "at row") final int ordinal) {
		return new StepSelector(guiPilot -> {
			final int tableIndex = ordinal - 1;
			getStepsTable(guiPilot).wait(StatePolling
					.<JTable>assertion(
							t -> Assertions.assertTrue(tableIndex < t.getRowCount(), () -> "Step does not exist"))
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
				.<JTable>assertion(
						c -> Assertions.assertTrue(c.getSelectedRowCount() > 0, () -> "Step must be selected"))
				.withReport(c -> "a step is selected")));
	}

}
