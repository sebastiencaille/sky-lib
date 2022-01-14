package ch.scaille.tcwriter.pilot.selenium.bdd;

import java.util.HashSet;
import java.util.Set;

import ch.scaille.tcwriter.pilot.selenium.SeleniumPilot;

public class Story {

	public static Set<String> EXECUTED_SCENARIO = new HashSet<>();

	private Scenario<?>[] scenarii;

	public Story(Scenario<?>... scenarii) {
		this.scenarii = scenarii;
	}

	public void runAll(SeleniumPilot pilot) {

		if (scenarii.length > 1) {
			Scenario<?>[] givenScenarii = new Scenario<?>[scenarii.length - 1];
			System.arraycopy(scenarii, 0, givenScenarii, 0, givenScenarii.length);
			new Story(givenScenarii).runAll(pilot);
		}
		run(pilot);
	}

	private void run(SeleniumPilot pilot) {
		Scenario<?> lastScenario = scenarii[scenarii.length - 1];
		String name = lastScenario.getName(pilot);
		if (EXECUTED_SCENARIO.contains(name)) {
			return;
		}
		EXECUTED_SCENARIO.add(name);

		for (Scenario<?> scenario : scenarii) {
			scenario.run(pilot, scenario == lastScenario);
		}
	}

}
