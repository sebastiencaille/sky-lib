package ch.scaille.tcwriter.it;

import ch.scaille.tcwriter.pilot.swing.ByName;
import ch.scaille.tcwriter.pilot.swing.JButtonPoller;
import ch.scaille.tcwriter.pilot.swing.JListPoller;
import ch.scaille.tcwriter.pilot.swing.JTablePoller;
import ch.scaille.tcwriter.pilot.swing.PagePilot;
import ch.scaille.tcwriter.pilot.swing.SwingPilot;

public class TCWriterPage extends PagePilot {

	@ByName("Actors")
	public JListPoller actors = null;

	@ByName("Actions")
	public JListPoller actions = null;

	@ByName("Selectors")
	public JListPoller selectors;

	@ByName("selector-valueTable")
	public JTablePoller selectorValue;

	@ByName("Parameters0")
	public JListPoller parameters0;

	@ByName("param0-valueTable")
	public JTablePoller parameters0Value;

	@ByName("StepsTable")
	public JTablePoller stepsTable;

	@ByName("AddStep")
	public JButtonPoller addStep;

	@ByName("ApplyStep")
	public JButtonPoller applyStep;

	@ByName("NewTC")
	public JButtonPoller newTC;

	@ByName("LoadTC")
	public JButtonPoller loadTC;

	@ByName("SaveTC")
	public JButtonPoller saveTC;

	public TCWriterPage(SwingPilot pilot) {
		super(pilot);
	}
}
