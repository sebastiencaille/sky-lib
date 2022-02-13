package ch.scaille.testing.bdd.definition;

import ch.scaille.testing.bdd.definition.Steps.Step;

public class TestDictionary<PP> {

	public Steps<PP> with(Step<PP> when, Step<PP> then) {
		return with(null, when, then);
	}

	public Steps<PP> with(Step<PP> given, Step<PP> when, Step<PP> then) {
		return new Steps<>(given, when, then);
	}

	public Scenario<PP> scenario(Steps<PP> scenario) {
		return new Scenario<>(scenario);
	}

}
