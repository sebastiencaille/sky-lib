package ch.scaille.example.gui.controller;

import ch.scaille.tcwriter.pilot.swing.ByName;
import ch.scaille.tcwriter.pilot.swing.JLabelPilot;
import ch.scaille.tcwriter.pilot.swing.JListPilot;
import ch.scaille.tcwriter.pilot.swing.JTablePilot;
import ch.scaille.tcwriter.pilot.swing.JTextFieldPilot;
import ch.scaille.tcwriter.pilot.swing.JToggleButtonPilot;
import ch.scaille.tcwriter.pilot.swing.PagePilot;
import ch.scaille.tcwriter.pilot.swing.SwingPilot;

public class ControllerExamplePage extends PagePilot {

	@ByName("booleanEditor")
	public JToggleButtonPilot booleanEditor = null;

	@ByName("booleanEditorCheck")
	public JLabelPilot booleanEditorCheck = null;

	@ByName("intStringEditor")
	public JTextFieldPilot intStringEditor = null;

	@ByName("intCheck")
	public JLabelPilot intCheck = null;

	@ByName("dynamicListEditor")
	public JListPilot dynamicListEditor = null;

	@ByName("dynamicListSelectionCheck")
	public JLabelPilot dynamicListSelectionCheck = null;

	@ByName("staticListEditor")
	public JListPilot staticListEditor = null;

	@ByName("staticListSelectionCheck")
	public JLabelPilot staticListSelectionCheck = null;

	@ByName("tableSelectionEditor")
	public JTablePilot tableSelectionEditor = null;

	@ByName("tableSelectionCheck")
	public JLabelPilot tableSelectionCheck = null;

	public ControllerExamplePage(SwingPilot pilot) {
		super(pilot);
	}

}
