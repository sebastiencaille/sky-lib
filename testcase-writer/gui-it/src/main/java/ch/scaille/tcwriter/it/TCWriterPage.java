package ch.scaille.tcwriter.it;

import ch.scaille.testing.testpilot.swing.ByName;
import ch.scaille.testing.testpilot.swing.JButtonPoller;
import ch.scaille.testing.testpilot.swing.JListPoller;
import ch.scaille.testing.testpilot.swing.JTablePoller;
import ch.scaille.testing.testpilot.swing.PagePilot;
import ch.scaille.testing.testpilot.swing.SwingPilot;

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
