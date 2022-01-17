package ch.scaille.testing.bdd.definition;

public class Story<P> {


	private Scenario<P, ?>[] scenarii;

	public Story(Scenario<P, ?>... scenarii) {
		this.scenarii = scenarii;
	}
	
	public Scenario<P, ?>[] getScenarii() {
		return scenarii;
	}

	public void run(P pilot) {
		Scenario<P, ?> lastScenario = scenarii[scenarii.length - 1];
		for (Scenario<P, ?> scenario : scenarii) {
			scenario.run(pilot, scenario == lastScenario);
		}
	}

}
