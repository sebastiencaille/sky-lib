package ch.skymarshall.example.gui.controller;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import ch.skymarshall.example.gui.controller.impl.ControllerExampleController;
import ch.skymarshall.example.gui.controller.impl.ControllerExampleView;
import ch.skymarshall.tcwriter.pilot.swing.GuiPilot;
import ch.skymarshall.tcwriter.pilot.swing.SwingLabel;
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
		new SwingToggleButton(pilot, "BooleanCB").setSelected(true);
		new SwingLabel(pilot, "BooleanCBLabel").waitEnabled();
		new SwingToggleButton(pilot, "BooleanCB").setSelected(false);
		new SwingLabel(pilot, "BooleanCBLabel").waitDisabled();

		System.out.println(pilot.getActionReport().getFormattedReport());
	}

}
