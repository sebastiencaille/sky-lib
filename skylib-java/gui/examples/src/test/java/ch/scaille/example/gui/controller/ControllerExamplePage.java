package ch.scaille.example.gui.controller;

import ch.scaille.testing.testpilot.swing.ByName;
import ch.scaille.testing.testpilot.swing.JLabelAssertionBuilder;
import ch.scaille.testing.testpilot.swing.JListAssertionBuilder;
import ch.scaille.testing.testpilot.swing.JTableAssertionBuilder;
import ch.scaille.testing.testpilot.swing.JTextFieldAssertionBuilder;
import ch.scaille.testing.testpilot.swing.JToggleButtonAssertionBuilder;
import ch.scaille.testing.testpilot.swing.PagePilot;
import ch.scaille.testing.testpilot.swing.SwingPilot;

public class ControllerExamplePage extends PagePilot {

	@ByName("booleanEditor")
	public JToggleButtonAssertionBuilder booleanEditor = null;

	@ByName("booleanEditorCheck")
	public JLabelAssertionBuilder booleanEditorCheck = null;

	@ByName("intStringEditor")
	public JTextFieldAssertionBuilder intStringEditor = null;

	@ByName("intCheck")
	public JLabelAssertionBuilder intCheck = null;

	@ByName("dynamicListEditor")
	public JListAssertionBuilder dynamicListEditor = null;

	@ByName("dynamicListSelectionCheck")
	public JLabelAssertionBuilder dynamicListSelectionCheck = null;

	@ByName("staticListEditor")
	public JListAssertionBuilder staticListEditor = null;

	@ByName("staticListSelectionCheck")
	public JLabelAssertionBuilder staticListSelectionCheck = null;

	@ByName("tableSelectionEditor")
	public JTableAssertionBuilder tableSelectionEditor = null;

	@ByName("tableSelectionCheck")
	public JLabelAssertionBuilder tableSelectionCheck = null;

	public ControllerExamplePage(SwingPilot pilot) {
		super(pilot);
	}

}
