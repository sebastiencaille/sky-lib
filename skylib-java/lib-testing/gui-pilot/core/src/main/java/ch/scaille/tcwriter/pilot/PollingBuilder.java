package ch.scaille.tcwriter.pilot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import ch.scaille.tcwriter.pilot.PollingResult.FailureHandler;
import ch.scaille.tcwriter.pilot.factories.FailureHandlers;
import ch.scaille.tcwriter.pilot.factories.Pollings;

public class PollingBuilder<G extends AbstractComponentPilot<G, C>, C> {

	private final AbstractComponentPilot<G, C> pilot;
	private List<Consumer<Polling<C, ?>>> configurer = new ArrayList<>(2);

	public PollingBuilder(AbstractComponentPilot<G, C> elementPilot) {
		this.pilot = elementPilot;
	}

	public PollingBuilder<G, C> withConfiguration(Consumer<Polling<C, ?>> configurer) {
		this.configurer.add(configurer);
		return this;
	}

	public <U> ResultHandler<U> poll(final Polling<C, U> polling) {
		configurer.forEach(conf -> conf.accept(polling));
		return new ResultHandler<>(pilot.waitPollingSuccess(polling));
	}

	public ResultHandler<Boolean> satisfy(Predicate<C> check) {
		return poll(Pollings.satisfies(check));
	}
	
	public ResultHandler<Boolean> apply(Consumer<C> action) {
		return poll(Pollings.applies(action));
	}

	public ResultHandler<Boolean> asserts(Consumer<PollingContext<C>> action) {
		return poll(Pollings.asserts(action));
	}

	public class ResultHandler<R> {

		protected final PollingResult<C, R> pollingResult;

		public ResultHandler(PollingResult<C, R> pollingResult) {
			this.pollingResult = pollingResult;
		}

		public ResultHandler<R> andThen(ActionDelay delay) {
			pollingResult.getPolling().withExtraDelay(delay);
			return this;
		}
		
		/**
		 * Waits until a component is checked/edited
		 * 
		 * @param onFail failure behavior
		 */
		public R or(final FailureHandler<C, R, R> onFail) {
			return pilot.processResult(pollingResult, Function.identity(), onFail);
		}

		/**
		 * Waits until a component is edited, throwing a java assertion error in case of
		 * failure
		 */
		public R orFail() {
			return or(FailureHandlers.throwError());
		}

		public R orFail(String report) {
			return or(FailureHandlers.throwError(report));
		}

		public boolean isSatisfied() {
			return pilot.processResult(pollingResult, r -> Boolean.TRUE, FailureHandlers.ignoreFailure())
					.booleanValue();
		}

		public boolean isSatisfiedOr(String report) {
			return pilot.processResult(pollingResult, r -> Boolean.TRUE, FailureHandlers.reportNotSatisfied(report))
					.booleanValue();
		}
	}

	/**
	 * No precondition tested
	 *
	 * @return a precondition that is always true
	 */
	static <C> Predicate<C> none() {
		return p -> true;
	}

}