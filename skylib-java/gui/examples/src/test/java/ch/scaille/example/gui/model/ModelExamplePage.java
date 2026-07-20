package ch.scaille.example.gui.model;

import ch.scaille.testing.testpilot.swing.ByName;
import ch.scaille.testing.testpilot.swing.JTablePoller;
import ch.scaille.testing.testpilot.swing.JToggleButtonPoller;
import ch.scaille.testing.testpilot.swing.PagePilot;
import ch.scaille.testing.testpilot.swing.SwingPilot;

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
