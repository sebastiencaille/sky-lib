package ch.scaille.example.gui.model;

import ch.scaille.testing.testpilot.swing.ByName;
import ch.scaille.testing.testpilot.swing.JTableAssertionBuilder;
import ch.scaille.testing.testpilot.swing.JToggleButtonAssertionBuilder;
import ch.scaille.testing.testpilot.swing.PagePilot;
import ch.scaille.testing.testpilot.swing.SwingPilot;

public class ModelExamplePage extends PagePilot {

	@ByName("reverseOrder")
	public JToggleButtonAssertionBuilder reverseOrder = null;

	@ByName("enableFilter")
	public JToggleButtonAssertionBuilder enableFilter = null;

	@ByName("listTable")
	public JTableAssertionBuilder listTable = null;

	public ModelExamplePage(SwingPilot pilot) {
		super(pilot);
	}
}
