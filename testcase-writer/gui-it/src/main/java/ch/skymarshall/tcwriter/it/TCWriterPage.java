package ch.skymarshall.tcwriter.it;

import ch.skymarshall.tcwriter.pilot.swing.ByName;
import ch.skymarshall.tcwriter.pilot.swing.JButtonPilot;
import ch.skymarshall.tcwriter.pilot.swing.JListPilot;
import ch.skymarshall.tcwriter.pilot.swing.JTablePilot;
import ch.skymarshall.tcwriter.pilot.swing.PagePilot;
import ch.skymarshall.tcwriter.pilot.swing.SwingPilot;

public class TCWriterPage extends PagePilot {

	@ByName("Actors")
	public JListPilot actors = null;

	@ByName("Actions")
	public JListPilot actions = null;

	@ByName("Selectors")
	public JListPilot selectors;

	@ByName("selector-valueTable")
	public JTablePilot selectorValue;

	@ByName("Parameters0")
	public JListPilot parameters0;

	@ByName("param0-valueTable")
	public JTablePilot parameters0Value;

	@ByName("StepsTable")
	public JTablePilot stepsTable;

	@ByName("AddStep")
	public JButtonPilot addStep;

	@ByName("ApplyStep")
	public JButtonPilot applyStep;

	public TCWriterPage(SwingPilot pilot) {
		super(pilot);
		initialize();
	}
}
