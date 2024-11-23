package ch.scaille.example.gui.model;

import ch.scaille.tcwriter.pilot.swing.ByName;
import ch.scaille.tcwriter.pilot.swing.JTablePoller;
import ch.scaille.tcwriter.pilot.swing.JToggleButtonPoller;
import ch.scaille.tcwriter.pilot.swing.PagePilot;
import ch.scaille.tcwriter.pilot.swing.SwingPilot;

public class ModelExamplePage extends PagePilot {

	@ByName("reverseOrder")
	public JToggleButtonPoller reverseOrder = null;

	@ByName("enableFilter")
	public JToggleButtonPoller enableFilter = null;

	@ByName("listTable")
	public JTablePoller listTable = null;

	public ModelExamplePage(SwingPilot pilot) {
		super(pilot);
	}
}
