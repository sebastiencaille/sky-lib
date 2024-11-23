package ch.scaille.tcwriter.it.api;

import static ch.scaille.tcwriter.pilot.factories.Reporting.checkingThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;

import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.it.TCWriterPage;

@TCApi(description = "Step selector", humanReadable = "Step selector", isSelector = true)
public interface StepSelector extends Consumer<TCWriterPage> {

	@TCApi(description = "Step at index", humanReadable = "select the step %s")
	static StepSelector selectStep(@TCApi(description = "index", humanReadable = "at row") final int ordinal) {
		return page -> {
			final int tableIndex = ordinal - 1;
			final var stepsTable = page.stepsTable;
			stepsTable.fail(checkingThat("the step " + ordinal + " exists"))
					.ifNot()
					.asserted(pc -> assertTrue(tableIndex < pc.getComponent().getRowCount(), "Step must exist"));
			stepsTable.selectRow(tableIndex);
		};

	}

	@TCApi(description = "Append a step to the test", humanReadable = "add a step to the test case")
	static StepSelector addStep() {
		return page -> {
			final var stepsTable = page.stepsTable;
			stepsTable.fail("selecting the last step").ifNot().appliedCtxt(pc -> {
				final var table = pc.getComponent();
				final int stepsCount = table.getRowCount();
				if (stepsCount > 0) {
					table.setRowSelectionInterval(stepsCount - 1, stepsCount - 1);
				}
			});
			page.addStep.click();
		};

	}

	@TCApi(description = "Selected step", humanReadable = "")
	static StepSelector currentStep() {
		return page -> page.stepsTable.fail(checkingThat("a step is selected"))
				.ifNot()
				.asserted(pc -> assertTrue(pc.getComponent().getSelectedRowCount() > 0));
	}

}
