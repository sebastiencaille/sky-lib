package ch.skymarshall.tcwriter.it;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.junit.Assert;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.swingpilot.GuiPilot;
import ch.skymarshall.util.helpers.NoExceptionCloseable;

@TCApi(description = "Step selector", humanReadable = "Step selector", isSelector = true)
public abstract class StepSelector {

	protected abstract void apply(GuiPilot guiPilot);

	public void select(final GuiPilot guiPilot) {
		try (NoExceptionCloseable closeable = guiPilot.withDialogBoxCloser()) {
			SwingUtilities.invokeAndWait(() -> apply(guiPilot));
		} catch (final InvocationTargetException e) {
			Assert.assertNull("Unexpected error while executing selection", e.getCause());
		} catch (final InterruptedException e) {
			Assert.assertNull("Unexpected error while executing selection", e);
		}
	}

	protected JTable getStepsTable(final GuiPilot guiPilot) {
		return guiPilot.getComponent(TCGuiPilot.STEPS_TABLE, JTable.class);
	}

	@TCApi(description = "Select a step", humanReadable = "select the step %i")
	public static StepSelector selectStep(@TCApi(description = "index", humanReadable = "at row") final int ordinal) {
		return new StepSelector() {

			@Override
			public void apply(final GuiPilot guiPilot) {
				final JTable stepsTable = getStepsTable(guiPilot);
				Assert.assertTrue("Step does not exist", ordinal < stepsTable.getRowCount());
				stepsTable.setRowSelectionInterval(ordinal, ordinal);
			}
		};
	}

	@TCApi(description = "Append a step to the test", humanReadable = "add a step to the test case")
	public static StepSelector addStep() {
		return new StepSelector() {

			@Override
			public void apply(final GuiPilot guiPilot) {
				final JTable stepsTable = getStepsTable(guiPilot);
				final int stepsCount = stepsTable.getRowCount();
				if (stepsCount > 0) {
					stepsTable.setRowSelectionInterval(stepsCount - 1, stepsCount - 1);
				}
				guiPilot.getComponent(TCGuiPilot.ADD_STEP, JButton.class).doClick();
			}
		};

	}

}
