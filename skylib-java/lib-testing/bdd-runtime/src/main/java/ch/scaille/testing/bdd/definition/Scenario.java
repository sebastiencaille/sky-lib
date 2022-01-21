package ch.scaille.testing.bdd.definition;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.scaille.testing.bdd.definition.Story.Context;
import ch.scaille.testing.bdd.definition.Story.StoryContext;

/**
 * 
 * @author scaille
 *
 * @param <P>  Shared type (like Swing/Selenium Pilot)
 * @param <PP> Specific type (like PagePilots)
 */
public class Scenario<P, PP> {

	public static class Step<PP> {
		public final String description;
		private final BiConsumer<PP, Context> call;

		public Step(String description, BiConsumer<PP, Context> stepCall) {
			super();
			this.description = description;
			this.call = stepCall;
		}

	}

	private final Function<P, PP> pageSupplier;

	private Step<PP> givenStep;
	private Step<PP> whenStep;
	private Step<PP> thenStep;

	protected Scenario(Function<P, PP> pageSupplier, Step<PP> givenStep, Step<PP> whenStep, Step<PP> thenStep) {
		this.pageSupplier = pageSupplier;
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

	public void run(P pilot, Context context, boolean isLastScenario) {
		PP page = pageSupplier.apply(pilot);
		given(page, context);
		when(page, context, isLastScenario);
		if (isLastScenario) {
			then(page, context);
		}
	}

	public void given(PP page, Context context) {
		if (givenStep != null) {
			context.getContext(StoryContext.class).addGiven(givenStep);
			givenStep.call.accept(page, context);
		}
	}

	public void when(PP page, Context context, boolean isLastScenario) {
		if (isLastScenario) {
			context.getContext(StoryContext.class).addWhen(whenStep);
		} else {
			context.getContext(StoryContext.class).addGiven(whenStep);
		}
		whenStep.call.accept(page, context);
	}

	public void then(PP page, Context context) {
		context.getContext(StoryContext.class).addThen(thenStep);
		thenStep.call.accept(page, context);
	}

	public static <P, PP> Step<PP> step(String description, Consumer<PP> code) {
		return new Step<>(description, (p, c) -> code.accept(p));
	}

	public static <P, PP> Step<PP> step(String description, BiConsumer<PP, Context> code) {
		return new Step<>(description, code);
	}

	public static class ScenarioFactory<P, PP> {

		private final Function<P, PP> pageSupplier;

		public ScenarioFactory(Function<P, PP> pageSupplier) {
			this.pageSupplier = pageSupplier;
		}

		public Scenario<P, PP> with(Step<PP> when, Step<PP> then) {
			return with(null, when, then);
		}

		public Scenario<P, PP> with(Step<PP> given, Step<PP> when, Step<PP> then) {
			return new Scenario<>(pageSupplier, given, when, then);
		}
	}

	public static <P, PP> ScenarioFactory<P, PP> of(Function<P, PP> pageSupplier) {
		return new ScenarioFactory<>(pageSupplier);
	}

}
