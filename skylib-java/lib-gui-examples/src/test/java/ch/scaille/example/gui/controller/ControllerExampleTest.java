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

		final var controller = new ControllerExampleController();
		final var view = new ControllerExampleView[1];
		EventQueue.invokeAndWait(() -> {
			view[0] = new ControllerExampleView(controller);
			view[0].setVisible(true);
		});

		final var pilot = new SwingPilot(view[0]);
		pilot.setDefaultPollingTimeout(Duration.ofSeconds(1));

		final var page = pilot.page(ControllerExamplePage::new);

		page.booleanEditor.setSelected(true);
		page.booleanEditorCheck.assertEnabled();
		page.booleanEditor.setSelected(false);
		page.booleanEditorCheck.assertDisabled();

		page.intStringEditor.setText("123");
		page.intCheck.assertTextEquals("123");
		page.intStringEditor.setText("abc");
		page.intCheck.assertTextEquals("123");
		page.intStringEditor.fail("foreground color should be RED").ifNot()
				.satisfied(c -> c.getForeground() == Color.RED);

		page.staticListEditor.select("A");
		page.staticListSelectionCheck.assertTextEquals("A");
		page.dynamicListEditor.select("C");
		page.dynamicListSelectionCheck.assertTextEquals("C");

		page.staticListEditor.select("B");
		page.staticListSelectionCheck.assertTextEquals("B");
		page.dynamicListEditor.assertSelected("C");
		page.dynamicListSelectionCheck.assertTextEquals("C");

		page.tableSelectionEditor.selectRow(0);
		page.tableSelectionCheck.assertTextEquals("Hello");
		page.tableSelectionEditor.selectRow(1);
		page.tableSelectionCheck.assertTextEquals("World");
		page.tableSelectionEditor.editValueOnSelectedRow(0, "Bouh");
		page.tableSelectionCheck.assertTextEquals("Bouh");

		Logs.of(this).info(pilot.getActionReport().getFormattedReport());
	}

}
