package ch.scaille.tcwriter.it;

import ch.scaille.testing.testpilot.swing.ByName;
import ch.scaille.testing.testpilot.swing.JButtonAssertionBuilder;
import ch.scaille.testing.testpilot.swing.JListAssertionBuilder;
import ch.scaille.testing.testpilot.swing.JTableAssertionBuilder;
import ch.scaille.testing.testpilot.swing.PagePilot;
import ch.scaille.testing.testpilot.swing.SwingPilot;

public class TCWriterPage extends PagePilot {

	@ByName("Actors")
	public JListAssertionBuilder actors;

	@ByName("Actions")
	public JListAssertionBuilder actions;

	@ByName("Selectors")
	public JListAssertionBuilder selectors;

	@ByName("selector-valueTable")
	public JTableAssertionBuilder selectorValue;

	@ByName("Parameters0")
	public JListAssertionBuilder parameters0;

	@ByName("param0-valueTable")
	public JTableAssertionBuilder parameters0Value;

	@ByName("StepsTable")
	public JTableAssertionBuilder stepsTable;

	@ByName("AddStep")
	public JButtonAssertionBuilder addStep;

	@ByName("ApplyStep")
	public JButtonAssertionBuilder applyStep;

	@ByName("NewTC")
	public JButtonAssertionBuilder newTC;

	@ByName("LoadTC")
	public JButtonAssertionBuilder loadTC;

	@ByName("SaveTC")
	public JButtonAssertionBuilder saveTC;

	public TCWriterPage(SwingPilot pilot) {
		super(pilot);
	}
}
