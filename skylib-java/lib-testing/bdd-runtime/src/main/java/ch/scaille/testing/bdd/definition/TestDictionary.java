package ch.scaille.testing.bdd.definition;

import ch.scaille.testing.bdd.definition.Steps.Step;

/**
 *
 * @param <A> Application test apis
 */
public class TestDictionary<A extends AbstractAppTestApi<?>> {

	public Steps<A> with(Step<A> when, Step<A> then) {
		return with(null, when, then);
	}

	public Steps<A> with(Step<A> given, Step<A> when, Step<A> then) {
		return new Steps<>(given, when, then);
	}

	public Scenario<A> scenario(Steps<A> scenario) {
		return new Scenario<>(scenario);
	}

}
