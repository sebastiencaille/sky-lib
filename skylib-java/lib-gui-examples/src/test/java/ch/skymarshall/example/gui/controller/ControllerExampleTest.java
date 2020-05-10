package ch.skymarshall.example.gui.controller;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

import org.junit.Test;

import ch.skymarshall.example.gui.controller.impl.ControllerExampleController;
import ch.skymarshall.example.gui.controller.impl.ControllerExampleView;
import ch.skymarshall.tcwriter.pilot.swing.SwingGuiPilot;
import ch.skymarshall.tcwriter.pilot.swing.SwingLabel;
import ch.skymarshall.tcwriter.pilot.swing.SwingList;
import ch.skymarshall.tcwriter.pilot.swing.SwingTable;
import ch.skymarshall.tcwriter.pilot.swing.SwingText;
import ch.skymarshall.tcwriter.pilot.swing.SwingToggleButton;

public class ControllerExampleTest {

	@Test
	public void testExample() throws InvocationTargetException, InterruptedException {

		final ControllerExampleController controller = new ControllerExampleController();
		final ControllerExampleView[] view = new ControllerExampleView[1];
		EventQueue.invokeAndWait(() -> {
			view[0] = new ControllerExampleView(controller);
			view[0].setVisible(true);
		});

		final SwingGuiPilot pilot = new SwingGuiPilot(view[0]);
		pilot.setDefaultActionTimeout(Duration.ofSeconds(1));
		booleanEditor(pilot).setSelected(true);
		booleanEditorCheck(pilot).waitEnabled();
		booleanEditor(pilot).setSelected(false);
		booleanEditorCheck(pilot).waitDisabled();

		intStringEditor(pilot).setText("123");
		intCheck(pilot).checkValue("123");
		intStringEditor(pilot).setText("abc");
		intCheck(pilot).checkValue("123");
		getErrorLabel(pilot).checkValue("Cannot convert to number");

		staticListEditor(pilot).select("A");
		staticListSelectionCheck(pilot).checkValue("A");
		dynamicListEditor(pilot).select("C");
		dynamicListSelectionCheck(pilot).checkValue("C");

		staticListEditor(pilot).select("B");
		staticListSelectionCheck(pilot).checkValue("B");
		dynamicListEditor(pilot).checkSelected("C");
		dynamicListSelectionCheck(pilot).checkValue("C");

		tableSelectionEditor(pilot).selectRow(0);
		tableSelectionCheck(pilot).checkValue("Hello");
		tableSelectionEditor(pilot).selectRow(1);
		tableSelectionCheck(pilot).checkValue("World");
		tableSelectionEditor(pilot).editValueOnSelectedRow(0, "Bouh");
		tableSelectionCheck(pilot).checkValue("Bouh");

		System.out.println(pilot.getActionReport().getFormattedReport());
	}

	private SwingToggleButton booleanEditor(final SwingGuiPilot pilot) {
		return new SwingToggleButton(pilot, "booleanEditor");
	}

	private SwingLabel booleanEditorCheck(final SwingGuiPilot pilot) {
		return new SwingLabel(pilot, "booleanEditorCheck");
	}

	private SwingText intStringEditor(final SwingGuiPilot pilot) {
		return new SwingText(pilot, "intStringEditor");
	}

	private final SwingLabel intCheck(final SwingGuiPilot pilot) {
		return new SwingLabel(pilot, "intCheck");
	}

	private SwingLabel getErrorLabel(final SwingGuiPilot pilot) {
		return new SwingLabel(pilot, "errorLabel");
	}

	private SwingList dynamicListEditor(final SwingGuiPilot pilot) {
		return new SwingList(pilot, "dynamicListEditor");
	}

	private SwingLabel dynamicListSelectionCheck(final SwingGuiPilot pilot) {
		return new SwingLabel(pilot, "dynamicListSelectionCheck");
	}

	private SwingList staticListEditor(final SwingGuiPilot pilot) {
		return new SwingList(pilot, "staticListEditor");
	}

	private SwingLabel staticListSelectionCheck(final SwingGuiPilot pilot) {
		return new SwingLabel(pilot, "staticListSelectionCheck");
	}

	private SwingTable tableSelectionEditor(final SwingGuiPilot pilot) {
		return new SwingTable(pilot, "tableSelectionEditor");
	}

	private SwingLabel tableSelectionCheck(final SwingGuiPilot pilot) {
		return new SwingLabel(pilot, "tableSelectionCheck");
	}

}
