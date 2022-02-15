package ch.scaille.testing.bdd.definition;

import java.util.function.Consumer;

import ch.scaille.testing.bdd.definition.Scenario.ExecutionContext;

/**
 * 
 * @author scaille
 *
 * @param <A> Application test apis
 */
public class Steps<A> {

	public static class Step<A> {
		public final String description;
		private final Consumer<A> call;

		public Step(String description, Consumer<A> stepCall) {
			super();
			this.description = description;
			this.call = stepCall;
		}

	}

	public static <A> Step<A> step(String description, Consumer<A> call) {
		return new Step<>(description, call);
	}

	private final Step<A> givenStep;
	private final Step<A> whenStep;
	private final Step<A> thenStep;

	protected Steps(Step<A> givenStep, Step<A> whenStep, Step<A> thenStep) {
		this.givenStep = givenStep;
		this.whenStep = whenStep;
		this.thenStep = thenStep;
	}

	public void run(ExecutionContext<A> context, boolean isLastScenario) {
		given(context);
		when(context, isLastScenario);
		if (isLastScenario) {
			then(context);
		}
	}

	public void given(ExecutionContext<A> context) {
		if (givenStep != null) {
			context.addGiven(givenStep);
			givenStep.call.accept(context.getAppTestApi());
		}
	}

	public void when(ExecutionContext<A> context, boolean isLastScenario) {
		if (isLastScenario) {
			context.addWhen(whenStep);
		} else {
			context.addGiven(whenStep);
		}
		whenStep.call.accept(context.getAppTestApi());
	}

	public void then(ExecutionContext<A> context) {
		context.addThen(thenStep);
		thenStep.call.accept(context.getAppTestApi());
	}

}
