package ch.scaille.example.gui.controller;

import ch.scaille.testing.testpilot.swing.ByName;
import ch.scaille.testing.testpilot.swing.JLabelPoller;
import ch.scaille.testing.testpilot.swing.JListPoller;
import ch.scaille.testing.testpilot.swing.JTablePoller;
import ch.scaille.testing.testpilot.swing.JTextFieldPoller;
import ch.scaille.testing.testpilot.swing.JToggleButtonPoller;
import ch.scaille.testing.testpilot.swing.PagePilot;
import ch.scaille.testing.testpilot.swing.SwingPilot;

public class ControllerExamplePage extends PagePilot {

	@ByName("booleanEditor")
	public JToggleButtonPoller booleanEditor = null;

	@ByName("booleanEditorCheck")
	public JLabelPoller booleanEditorCheck = null;

	@ByName("intStringEditor")
	public JTextFieldPoller intStringEditor = null;

	@ByName("intCheck")
	public JLabelPoller intCheck = null;

	@ByName("dynamicListEditor")
	public JListPoller dynamicListEditor = null;

	@ByName("dynamicListSelectionCheck")
	public JLabelPoller dynamicListSelectionCheck = null;

	@ByName("staticListEditor")
	public JListPoller staticListEditor = null;

	@ByName("staticListSelectionCheck")
	public JLabelPoller staticListSelectionCheck = null;

	@ByName("tableSelectionEditor")
	public JTablePoller tableSelectionEditor = null;

	@ByName("tableSelectionCheck")
	public JLabelPoller tableSelectionCheck = null;

	public ControllerExamplePage(SwingPilot pilot) {
		super(pilot);
	}

}
