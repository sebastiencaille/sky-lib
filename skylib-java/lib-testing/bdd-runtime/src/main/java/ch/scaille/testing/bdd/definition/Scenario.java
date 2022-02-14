package ch.scaille.testing.bdd.definition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import ch.scaille.testing.bdd.definition.Steps.Step;

public class Scenario<PP extends AbstractAppTestApi<?>> {

	public static class ExecutionContext<PP> {

		private final List<String> report = new ArrayList<>();
		private final PP appTestApi;

		public ExecutionContext(PP appTestApi) {
			this.appTestApi = appTestApi;
		}

		public PP getAppTestApi() {
			return appTestApi;
		}

		private void add(String verb, Step<?> step) {
			report.add(verb + step.description.replace("|", "\n  And "));
		}

		public void addGiven(Step<?> step) {
			if (report.isEmpty()) {
				add("Given ", step);
			} else {
				add("  And ", step);
			}
		}

		public void addWhen(Step<?> step) {
			add(" When ", step);
		}

		public void addThen(Step<?> step) {
			add(" Then ", step);
		}

		@Override
		public String toString() {
			return String.join("\n", report);
		}

	}

	private final Steps<PP>[] steps;

	private Consumer<PP> runConfiguration;

	public Scenario(Steps<PP>... steps) {
		this.steps = steps;
	}

	public Scenario<PP> beforeRun(Consumer<PP> runConfiguration) {
		this.runConfiguration = runConfiguration;
		return this;
	}

	public Steps<PP>[] getScenarii() {
		return steps;
	}

	public Scenario<PP> followedBy(Steps<PP>... nexts) {
		Steps<PP>[] newsteps = Arrays.copyOf(steps, steps.length + nexts.length);
		System.arraycopy(nexts, 0, newsteps, steps.length, nexts.length);
		return new Scenario<>(newsteps);
	}

	public ExecutionContext<PP> run(PP appTestApi) {
		ExecutionContext<PP> executionContext = new ExecutionContext<>(appTestApi);
		appTestApi.resetContext();
		if (runConfiguration != null) {
			runConfiguration.accept(appTestApi);
		}

		Steps<PP> lastStep = steps[steps.length - 1];
		for (Steps<PP> step : steps) {
			step.run(executionContext, step == lastStep);
		}
		return executionContext;
	}

}
