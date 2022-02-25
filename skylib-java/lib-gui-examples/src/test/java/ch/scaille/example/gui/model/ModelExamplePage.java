package ch.scaille.example.gui.model;

import ch.scaille.tcwriter.pilot.swing.ByName;
import ch.scaille.tcwriter.pilot.swing.JTablePilot;
import ch.scaille.tcwriter.pilot.swing.JToggleButtonPilot;
import ch.scaille.tcwriter.pilot.swing.PagePilot;
import ch.scaille.tcwriter.pilot.swing.SwingPilot;

public class ModelExamplePage extends PagePilot {

	@ByName("reverseOrder")
	public JToggleButtonPilot reverseOrder = null;

	@ByName("enableFilter")
	public JToggleButtonPilot enableFilter = null;

	@ByName("listTable")
	public JTablePilot listTable = null;

	public ModelExamplePage(SwingPilot pilot) {
		super(pilot);
	}
}
