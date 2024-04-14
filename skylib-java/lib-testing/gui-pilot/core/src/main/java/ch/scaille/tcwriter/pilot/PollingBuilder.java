package ch.scaille.tcwriter.pilot;

import static ch.scaille.tcwriter.pilot.factories.Pollings.applies;
import static ch.scaille.tcwriter.pilot.factories.Pollings.satisfies;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import ch.scaille.tcwriter.pilot.PollingResult.FailureHandler;
import ch.scaille.tcwriter.pilot.factories.FailureHandlers;
import ch.scaille.tcwriter.pilot.factories.Pollings;

/**
 * Rules: 
 * <ul>
 * <li>try... methods require using orFail(...) or satisfied(...)</li>
 * <li>...Poll methods take a Polling as parameter</li>
 * <li>...Apply methods take a Component Consumer as parameter</li>  
 * <li>...Satisfy methods take a Predicate as parameter</li>
 * <li>...Assert methods take a Context Consumer as parameter (mostly to access the descriptions)</li>
 * <li>Otherwise, the method should call orFail(...)</li>
 * </ul>
 * 
 * @param <P> type of Pilot
 * @param <C> the Component type
 */
public class PollingBuilder<P extends AbstractComponentPilot<P, C>, C> {

	private final AbstractComponentPilot<P, C> pilot;
	private List<Consumer<Polling<C, ?>>> configurer = new ArrayList<>(2);

	public PollingBuilder(AbstractComponentPilot<P, C> elementPilot) {
		this.pilot = elementPilot;
	}

	public PollingBuilder<P, C> withConfiguration(Consumer<Polling<C, ?>> configurer) {
		this.configurer.add(configurer);
		return this;
	}

	public <U> ResultHandler<U> tryPoll(final Polling<C, U> polling) {
		configurer.forEach(conf -> conf.accept(polling));
		return new ResultHandler<>(pilot.waitPollingSuccess(polling));
	}

	public ResultHandler<Boolean> trySatisfy(Predicate<C> check) {
		return tryPoll(satisfies(check));
	}

	public ResultHandler<Boolean> tryApply(Consumer<C> action) {
		return tryPoll(applies(action));
	}

	public ResultHandler<Boolean> tryAssert(Consumer<PollingContext<C>> action) {
		return tryPoll(Pollings.asserts(action));
	}

	public void pollOrFail(final Polling<C, ?> polling) {
		tryPoll(polling).orFail();
	}

	public void satisfyOrFail(Predicate<C> check) {
		pollOrFail(satisfies(check));
	}

	public void applyOrFail(Consumer<C> action) {
		pollOrFail(applies(action));
	}

	public void assertOrFail(Consumer<PollingContext<C>> action) {
		pollOrFail(Pollings.asserts(action));
	}

	public boolean isSatisfied(final Polling<C, ?> polling) {
		return tryPoll(polling).isSatisfied();
	}

	public boolean isSatisfied(Predicate<C> check) {
		return isSatisfied(satisfies(check));
	}

	public boolean applySatisfied(Consumer<C> action) {
		return isSatisfied(applies(action));
	}

	public <V> V get(Function<C, V> getter) {
		return tryPoll(Pollings.get(getter)).value();
	}

	public class ResultHandler<R> {

		protected final PollingResult<C, R> pollingResult;

		public ResultHandler(PollingResult<C, R> pollingResult) {
			this.pollingResult = pollingResult;
		}

		public R value() {
			return pilot.processResult(pollingResult, Function.identity(), FailureHandlers.throwError());
		}

		public Optional<R> optional() {
			return Optional.ofNullable(pilot.processResult(pollingResult, Function.identity(), FailureHandlers.returnNull()));
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

}