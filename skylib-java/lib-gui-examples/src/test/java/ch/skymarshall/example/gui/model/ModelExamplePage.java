package ch.skymarshall.example.gui.model;

import ch.skymarshall.tcwriter.pilot.swing.ByName;
import ch.skymarshall.tcwriter.pilot.swing.JTablePilot;
import ch.skymarshall.tcwriter.pilot.swing.JToggleButtonPilot;
import ch.skymarshall.tcwriter.pilot.swing.PagePilot;
import ch.skymarshall.tcwriter.pilot.swing.SwingPilot;

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
