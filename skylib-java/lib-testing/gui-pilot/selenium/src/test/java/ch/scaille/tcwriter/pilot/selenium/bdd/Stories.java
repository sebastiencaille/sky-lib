package ch.scaille.tcwriter.pilot.selenium.bdd;

import ch.scaille.tcwriter.pilot.selenium.SeleniumPilot;
import ch.scaille.testing.bdd.definition.Story;

public class Stories {

	public static final Story<SeleniumPilot> POC_STORY = new Story<>(ExamplePage.SCENARIO_CONNECT,
			ExamplePage.SCENARIO_ENABLE, ExamplePage.SCENARIO_ALERT);

}
