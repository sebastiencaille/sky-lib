package ch.skymarshall.example.gui.controller;

import java.awt.Color;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.skymarshall.example.gui.controller.impl.ControllerExampleController;
import ch.skymarshall.example.gui.controller.impl.ControllerExampleView;
import ch.skymarshall.tcwriter.jupiter.DisabledIfHeadless;
import ch.skymarshall.tcwriter.pilot.swing.JLabelPilot;
import ch.skymarshall.tcwriter.pilot.swing.JListPilot;
import ch.skymarshall.tcwriter.pilot.swing.JTablePilot;
import ch.skymarshall.tcwriter.pilot.swing.JTextFieldPilot;
import ch.skymarshall.tcwriter.pilot.swing.JToggleButtonPilot;
import ch.skymarshall.tcwriter.pilot.swing.SwingPilot;
import ch.skymarshall.util.helpers.Log;

@ExtendWith(DisabledIfHeadless.class)
class ControllerExampleTest {

	@Test
	void testExample() throws InvocationTargetException, InterruptedException {

		final ControllerExampleController controller = new ControllerExampleController();
		final ControllerExampleView[] view = new ControllerExampleView[1];
		EventQueue.invokeAndWait(() -> {
			view[0] = new ControllerExampleView(controller);
			view[0].setVisible(true);
		});

		final SwingPilot pilot = new SwingPilot(view[0]);
		pilot.setDefaultActionTimeout(Duration.ofSeconds(1));
		booleanEditor(pilot).setSelected(true);
		booleanEditorCheck(pilot).waitEnabled();
		booleanEditor(pilot).setSelected(false);
		booleanEditorCheck(pilot).waitDisabled();

		intStringEditor(pilot).setText("123");
		intCheck(pilot).checkValue("123");
		intStringEditor(pilot).setText("abc");
		intCheck(pilot).checkValue("123");
		intStringEditor(pilot).check("foreground color is RED", c -> c.getForeground() == Color.RED);

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

		Log.of(this).info(pilot.getActionReport().getFormattedReport());
	}

	private JToggleButtonPilot booleanEditor(final SwingPilot pilot) {
		return pilot.toggleButton("booleanEditor");
	}

	private JLabelPilot booleanEditorCheck(final SwingPilot pilot) {
		return pilot.label("booleanEditorCheck");
	}

	private JTextFieldPilot intStringEditor(final SwingPilot pilot) {
		return pilot.text("intStringEditor");
	}

	private final JLabelPilot intCheck(final SwingPilot pilot) {
		return pilot.label("intCheck");
	}

	private JListPilot dynamicListEditor(final SwingPilot pilot) {
		return pilot.list("dynamicListEditor");
	}

	private JLabelPilot dynamicListSelectionCheck(final SwingPilot pilot) {
		return pilot.label("dynamicListSelectionCheck");
	}

	private JListPilot staticListEditor(final SwingPilot pilot) {
		return pilot.list("staticListEditor");
	}

	private JLabelPilot staticListSelectionCheck(final SwingPilot pilot) {
		return pilot.label("staticListSelectionCheck");
	}

	private JTablePilot tableSelectionEditor(final SwingPilot pilot) {
		return pilot.table("tableSelectionEditor");
	}

	private JLabelPilot tableSelectionCheck(final SwingPilot pilot) {
		return pilot.label("tableSelectionCheck");
	}

}
