package ch.scaille.testing.testpilot;

import static ch.scaille.testing.testpilot.factories.Pollings.applies;
import static ch.scaille.testing.testpilot.factories.Pollings.appliesCtxt;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import ch.scaille.testing.testpilot.PilotReport.ReportFunction;
import ch.scaille.testing.testpilot.factories.FailureHandlers;
import ch.scaille.testing.testpilot.factories.PollingResults;
import ch.scaille.testing.testpilot.factories.Pollings;
import ch.scaille.testing.testpilot.factories.FailureHandlers.FailureHandler;

/**
 * To build a polling
 * <p>
 * The idea is to
 * <ol>
 * <li>Create a PollingBuilder on a component.</li>
 * <li>Specifies how a failure is handled.</li>
 * <li>Configure the poller.</li>
 * <li>Execute the polling.</li>
 * </ol>
 * Example: on(myComponent).failUnless().clicked();
 * </p>
 * 
 * @param <C> the Component type
 * @param <T> the Builder (sub)type
 * @param <U> the Poller (sub)type
 */
public class PollingBuilder<C, T extends PollingBuilder<C, T, U>, U extends PollingBuilder.Poller<C>> {

	public static class Poller<C> {

		protected final PollingBuilder<C, ?, ?> builder;

		protected Poller(PollingBuilder<C, ?, ?> builder) {
			this.builder = builder;
		}

		public Poller<C> configure(Consumer<Polling<C, ?>> configurer) {
			builder.configurers.add(configurer);
			return this;
		}

		public boolean satisfied(final Polling<C, Boolean> polling) {
			return builder.poll(polling).isSuccess();
		}

		public boolean satisfied(Predicate<C> predicate) {
			return satisfied(Pollings.satisfies(predicate));
		}

		public boolean satisfiedCtxt(Predicate<PollingContext<C>> action) {
			return satisfied(new Polling<>(ctxt -> PollingResults.value(action.test(ctxt))));
		}

		public boolean applied(Consumer<C> consumer) {
			return satisfied(applies(consumer));
		}

		public boolean appliedCtxt(Consumer<PollingContext<C>> consumer) {
			return satisfied(appliesCtxt(consumer));
		}

		public boolean asserted(Consumer<C> assertion) {
			return applied(assertion);
		}
		
		public boolean assertedCtxt(Consumer<PollingContext<C>> assertion) {
			return appliedCtxt(assertion);
		}

		public <R> Optional<R> get(Function<C, R> getter) {
			final var pollResult = builder.poll(Pollings.get(getter));
			if (pollResult.isSuccess()) {
				return Optional.ofNullable(pollResult.polledValue());
			}
			return Optional.empty();
		}

	}

	protected final AbstractComponentPilot<C> pilot;

	protected final List<Consumer<Polling<C, ?>>> configurers = new ArrayList<>(2);

	private FailureHandler<C, ?> failureHandler;

	public class Configurer<F> {

		public F withConfig(Consumer<Polling<C, ?>> configurer) {
			configurers.add(configurer);
			return (F) this;
		}

		public F timingOut(Duration timeout) {
			withConfig(polling -> polling.withDelay(timeout));
			return (F) this;
		}

	}

	public class UnlessConfigurer extends Configurer<UnlessConfigurer> {

		public U unless() {
			return createPoller();
		}

	}

	public class ThatConfigurer extends Configurer<ThatConfigurer> {

		/**
		 * Waits until a condition is applied
		 */
		public U that() {
			return createPoller();
		}

	}

	protected U createPoller() {
		return (U) new Poller<>(this);
	}
	
	protected <R> PollingResult<C, R> poll(final Polling<C, R> polling) {
		configurers.forEach(conf -> conf.accept(polling));
		final var pollingResult = pilot.processResult(pilot.waitPollingSuccess(polling), PollingResults.identity(),
				((FailureHandler<C, R>) failureHandler));
		reset();
		return pollingResult;
	}

	public PollingBuilder(AbstractComponentPilot<C> pilot) {
		this.pilot = pilot;
	}

	public void reset() {
		failureHandler = null;
		configurers.clear();
	}

	/**
	 * Waits until a condition is applied, throwing a java assertion error in case
	 * of failure
	 */
	public UnlessConfigurer fail() {
		this.failureHandler = FailureHandlers.throwError();
		return new UnlessConfigurer();
	}

	/**
	 * Waits until a condition is applied, throwing a java assertion error in case
	 * of failure
	 * 
	 * @param assertion the text of the assertion
	 */
	public UnlessConfigurer fail(String assertion) {
		this.failureHandler = FailureHandlers.throwError(assertion);
		return new UnlessConfigurer();
	}

	public UnlessConfigurer fail(ReportFunction<C> reportFunction) {
		return fail().withConfig(polling -> polling.withReportFunction(reportFunction));
	}

	public U failUnless() {
		return fail().unless();
	}

	/**
	 * Waits until a condition is applied, skipping the error in case of failure
	 */
	public ThatConfigurer evaluate() {
		this.failureHandler = FailureHandlers.ignoreFailure();
		return new ThatConfigurer();
	}

	public U evaluateThat() {
		return evaluate().that();
	}

	public UnlessConfigurer report(String report) {
		this.failureHandler = FailureHandlers.reportFailure(report);
		return new UnlessConfigurer();
	}

}