package ch.scaille.tcwriter.it.api;

import static ch.scaille.tcwriter.pilot.Factories.Reporting.checkingThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;

import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.it.TCWriterPage;

@TCApi(description = "Step selector", humanReadable = "Step selector", isSelector = true)
@SuppressWarnings("java:S5960")
public interface StepSelector extends Consumer<TCWriterPage> {

	@TCApi(description = "Step at index", humanReadable = "select the step %s")
    static StepSelector selectStep(@TCApi(description = "index", humanReadable = "at row") final int ordinal) {
		return page -> {
			final int tableIndex = ordinal - 1;
			final var stepsTable = page.stepsTable;
			stepsTable.polling(stepsTable
					.asserts(pc -> assertTrue(tableIndex < pc.component.getRowCount(), () -> "Step must exist")))
					.orFail(checkingThat("the step " + ordinal + " exists"));
			stepsTable.selectRow(tableIndex);
		};

	}

	@TCApi(description = "Append a step to the test", humanReadable = "add a step to the test case")
    static StepSelector addStep() {
		return page -> {
			final var stepsTable = page.stepsTable;
			stepsTable.polling(stepsTable.applies(t -> {
				final int stepsCount = t.getRowCount();
				if (stepsCount > 0) {
					t.setRowSelectionInterval(stepsCount - 1, stepsCount - 1);
				}
			})).orFail("selecting the last step");
			page.addStep.click();
		};

	}

	@TCApi(description = "Selected step", humanReadable = "")
    static StepSelector currentStep() {
		return page -> page.stepsTable.polling(page.stepsTable
				.asserts(pc -> assertTrue(pc.component.getSelectedRowCount() > 0, () -> "Step must be selected")))
				.orFail(checkingThat("a step is selected"));
	}

}
