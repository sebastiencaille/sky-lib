package ch.skymarshall.example.gui.controller;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import ch.skymarshall.example.gui.controller.impl.ControllerExampleController;
import ch.skymarshall.example.gui.controller.impl.ControllerExampleView;
import ch.skymarshall.tcwriter.pilot.swing.GuiPilot;
import ch.skymarshall.tcwriter.pilot.swing.SwingLabel;
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

		final GuiPilot pilot = new GuiPilot(view[0]);
		new SwingToggleButton(pilot, "booleanEditor").setSelected(true);
		new SwingLabel(pilot, "booleanEditorLabel").waitEnabled();
		new SwingToggleButton(pilot, "booleanEditor").setSelected(false);
		new SwingLabel(pilot, "booleanEditorLabel").waitDisabled();

		new SwingText(pilot, "intStringEditor").setText("123");
		new SwingLabel(pilot, "intStringEditorLabel").checkValue("123");
		new SwingText(pilot, "intStringEditor").setText("abc");
		new SwingLabel(pilot, "intStringEditorLabel").checkValue("123");
		new SwingLabel(pilot, "errorLabel").checkValue("Cannot convert to number");

		System.out.println(pilot.getActionReport().getFormattedReport());
	}

}
