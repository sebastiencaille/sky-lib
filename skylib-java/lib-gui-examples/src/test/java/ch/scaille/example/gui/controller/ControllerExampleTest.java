package ch.scaille.example.gui.controller;

import java.awt.Color;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.scaille.example.gui.controller.impl.ControllerExampleController;
import ch.scaille.example.gui.controller.impl.ControllerExampleView;
import ch.scaille.tcwriter.jupiter.DisabledIfHeadless;
import ch.scaille.tcwriter.pilot.swing.SwingPilot;
import ch.scaille.util.helpers.Logs;

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
		pilot.setDefaultPollingTimeout(Duration.ofSeconds(1));

		ControllerExamplePage page = pilot.page(ControllerExamplePage::new);

		page.booleanEditor.setSelected(true);
		page.booleanEditorCheck.waitEnabled();
		page.booleanEditor.setSelected(false);
		page.booleanEditorCheck.waitDisabled();

		page.intStringEditor.setText("123");
		page.intCheck.checkValue("123");
		page.intStringEditor.setText("abc");
		page.intCheck.checkValue("123");
		page.intStringEditor.wait(c -> c.getForeground() == Color.RED, "foreground color is RED");

		page.staticListEditor.select("A");
		page.staticListSelectionCheck.checkValue("A");
		page.dynamicListEditor.select("C");
		page.dynamicListSelectionCheck.checkValue("C");

		page.staticListEditor.select("B");
		page.staticListSelectionCheck.checkValue("B");
		page.dynamicListEditor.checkSelected("C");
		page.dynamicListSelectionCheck.checkValue("C");

		page.tableSelectionEditor.selectRow(0);
		page.tableSelectionCheck.checkValue("Hello");
		page.tableSelectionEditor.selectRow(1);
		page.tableSelectionCheck.checkValue("World");
		page.tableSelectionEditor.editValueOnSelectedRow(0, "Bouh");
		page.tableSelectionCheck.checkValue("Bouh");

		Logs.of(this).info(pilot.getActionReport().getFormattedReport());
	}

}
