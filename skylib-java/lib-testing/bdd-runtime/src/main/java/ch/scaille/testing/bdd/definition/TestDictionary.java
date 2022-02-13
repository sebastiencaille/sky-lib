package ch.scaille.testing.bdd.definition;

import ch.scaille.testing.bdd.definition.ScenarioFragment.Step;

public class TestDictionary<PP> {

	public ScenarioFragment<PP> with(Step<PP> when, Step<PP> then) {
		return with(null, when, then);
	}

	public ScenarioFragment<PP> with(Step<PP> given, Step<PP> when, Step<PP> then) {
		return new ScenarioFragment<>(given, when, then);
	}

	public Scenario<PP> scenario(ScenarioFragment<PP> scenario) {
		return new Scenario<>(scenario);
	}

}
