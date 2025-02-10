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

		page.booleanEditor.failUnless().setSelected(true);
		page.booleanEditorCheck.assertEnabled();
		page.booleanEditor.failUnless().setSelected(false);
		page.booleanEditorCheck.assertDisabled();

		page.intStringEditor.failUnless().setText("123");
		page.intCheck.failUnless().assertTextEquals("123");
		page.intStringEditor.failUnless().setText("abc");
		page.intCheck.failUnless().assertTextEquals("123");
		page.intStringEditor.fail("foreground color should be RED").unless()
				.satisfied(c -> c.getForeground() == Color.RED);

		page.staticListEditor.failUnless().select("A");
		page.staticListSelectionCheck.failUnless().assertTextEquals("A");
		page.dynamicListEditor.failUnless().select("C");
		page.dynamicListSelectionCheck.failUnless().assertTextEquals("C");

		page.staticListEditor.failUnless().select("B");
		page.staticListSelectionCheck.failUnless().assertTextEquals("B");
		page.dynamicListEditor.failUnless().assertSelected("C");
		page.dynamicListSelectionCheck.failUnless().assertTextEquals("C");

		page.tableSelectionEditor.failUnless().selectRow(0);
		page.tableSelectionCheck.failUnless().assertTextEquals("Hello");
		page.tableSelectionEditor.failUnless().selectRow(1);
		page.tableSelectionCheck.failUnless().assertTextEquals("World");
		page.tableSelectionEditor.failUnless().editValueOnSelectedRow(0, "Bouh");
		page.tableSelectionCheck.failUnless().assertTextEquals("Bouh");

		Logs.of(this).info(pilot.getActionReport().getFormattedReport());
	}

}
