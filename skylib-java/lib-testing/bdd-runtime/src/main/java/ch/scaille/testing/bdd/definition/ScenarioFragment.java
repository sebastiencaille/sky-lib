package ch.scaille.testing.bdd.definition;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import ch.scaille.testing.bdd.definition.Scenario.Context;
import ch.scaille.testing.bdd.definition.Scenario.ExecutionContext;

/**
 * 
 * @author scaille
 *
 * @param <P>  Shared type (like Swing/Selenium Pilot)
 */
public class ScenarioFragment<PP> {

	public static class Step<PP> {
		public final String description;
		private final BiConsumer<PP, Context> call;

		public Step(String description, BiConsumer<PP, Context> stepCall) {
			super();
			this.description = description;
			this.call = stepCall;
		}

	}

	private Step<PP> givenStep;
	private Step<PP> whenStep;
	private Step<PP> thenStep;

	protected ScenarioFragment( Step<PP> givenStep, Step<PP> whenStep, Step<PP> thenStep) {
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

	public void run(PP pageProvider, Context context, boolean isLastScenario) {
		given(pageProvider, context);
		when(pageProvider, context, isLastScenario);
		if (isLastScenario) {
			then(pageProvider, context);
		}
	}

	public void given(PP pageProvider, Context context) {
		if (givenStep != null) {
			context.getContext(ExecutionContext.class).addGiven(givenStep);
			givenStep.call.accept(pageProvider, context);
		}
	}

	public void when(PP pageProvider, Context context, boolean isLastScenario) {
		if (isLastScenario) {
			context.getContext(ExecutionContext.class).addWhen(whenStep);
		} else {
			context.getContext(ExecutionContext.class).addGiven(whenStep);
		}
		whenStep.call.accept(pageProvider, context);
	}

	public void then(PP page, Context context) {
		context.getContext(ExecutionContext.class).addThen(thenStep);
		thenStep.call.accept(page, context);
	}
	
	public static <PP> Step<PP> step(String description, Consumer<PP> code) {
		return new Step<>(description, (p, c) -> code.accept(p));
	}

	public static <PP> Step<PP> step(String description, BiConsumer<PP, Context> code) {
		return new Step<>(description, code);
	} 



}
