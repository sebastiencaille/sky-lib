package ch.scaille.testing.bdd.definition;

import java.util.function.Consumer;

import ch.scaille.testing.bdd.definition.Scenario.ExecutionContext;

/**
 * 
 * @author scaille
 *
 * @param <P> Shared type (like Swing/Selenium Pilot)
 */
public class Steps<PP> {

	public static class Step<PP> {
		public final String description;
		private final Consumer<PP> call;

		public Step(String description, Consumer<PP> stepCall) {
			super();
			this.description = description;
			this.call = stepCall;
		}

	}

	public static <PP> Step<PP> step(String description, Consumer<PP> call) {
		return new Step<>(description, call);
	}

	private final Step<PP> givenStep;
	private final Step<PP> whenStep;
	private final Step<PP> thenStep;

	protected Steps(Step<PP> givenStep, Step<PP> whenStep, Step<PP> thenStep) {
		this.givenStep = givenStep;
		this.whenStep = whenStep;
		this.thenStep = thenStep;
	}

	public String getWhenCodeDescription() {
		return toBddCodeDescription(whenStep.description);
	}

	public String toBddCodeDescription(String descr) {
		return descr.replace(' ', '_').toLowerCase();
	}

	public void run(ExecutionContext<PP> context, boolean isLastScenario) {
		given(context);
		when(context, isLastScenario);
		if (isLastScenario) {
			then(context);
		}
	}

	public void given(ExecutionContext<PP> context) {
		if (givenStep != null) {
			context.addGiven(givenStep);
			givenStep.call.accept(context.getAppTestApi());
		}
	}

	public void when(ExecutionContext<PP> context, boolean isLastScenario) {
		if (isLastScenario) {
			context.addWhen(whenStep);
		} else {
			context.addGiven(whenStep);
		}
		whenStep.call.accept(context.getAppTestApi());
	}

	public void then(ExecutionContext<PP> context) {
		context.addThen(thenStep);
		thenStep.call.accept(context.getAppTestApi());
	}

}
