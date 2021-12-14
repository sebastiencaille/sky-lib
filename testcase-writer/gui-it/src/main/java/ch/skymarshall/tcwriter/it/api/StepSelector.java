package ch.skymarshall.tcwriter.it.api;

import static ch.skymarshall.tcwriter.pilot.Factories.checkingThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.it.TCWriterPage;
import ch.skymarshall.tcwriter.pilot.swing.JTablePilot;

@TCApi(description = "Step selector", humanReadable = "Step selector", isSelector = true)
@SuppressWarnings("java:S5960")
public interface StepSelector extends Consumer<TCWriterPage> {

	@TCApi(description = "Step at index", humanReadable = "select the step %s")
	public static StepSelector selectStep(@TCApi(description = "index", humanReadable = "at row") final int ordinal) {
		return page -> {
			final int tableIndex = ordinal - 1;
			JTablePilot stepsTable = page.stepsTable;
			stepsTable.wait(stepsTable
					.assertion(pc -> assertTrue(tableIndex < pc.component.getRowCount(), () -> "Step must exist"))
					.withReportText(checkingThat("the step " + ordinal + " exists")));
			stepsTable.selectRow(tableIndex);
		};

	}

	@TCApi(description = "Append a step to the test", humanReadable = "add a step to the test case")
	public static StepSelector addStep() {
		return page -> {
			JTablePilot stepsTable = page.stepsTable;
			stepsTable.wait(stepsTable.action(t -> {
				final int stepsCount = t.getRowCount();
				if (stepsCount > 0) {
					t.setRowSelectionInterval(stepsCount - 1, stepsCount - 1);
				}
			}).withReportText("selecting the last step"));
			page.addStep.click();
		};

	}

	@TCApi(description = "Selected step", humanReadable = "")
	public static StepSelector currentStep() {
		return page -> page.stepsTable.wait(page.stepsTable
				.assertion(pc -> assertTrue(pc.component.getSelectedRowCount() > 0, () -> "Step must be selected"))
				.withReportText(checkingThat("a step is selected")));
	}

}
