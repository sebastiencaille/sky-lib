package ch.scaille.tcwriter.pilot;

import static ch.scaille.tcwriter.pilot.factories.Pollings.applies;
import static ch.scaille.tcwriter.pilot.factories.Pollings.appliesCtxt;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import ch.scaille.tcwriter.pilot.PilotReport.ReportFunction;
import ch.scaille.tcwriter.pilot.factories.FailureHandlers;
import ch.scaille.tcwriter.pilot.factories.FailureHandlers.FailureHandler;
import ch.scaille.tcwriter.pilot.factories.PollingResults;
import ch.scaille.tcwriter.pilot.factories.Pollings;

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
 * Example: on(myComponent).fail().ifNot().clicked();
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

		public boolean asserted(Consumer<PollingContext<C>> assertion) {
			return appliedCtxt(assertion);
		}

		public <R> Optional<R> get(Function<C, R> getter) {
			final var pollResult = builder.poll(Pollings.get(getter));
			if (pollResult.isSuccess()) {
				return Optional.ofNullable(pollResult.polledValue);
			}
			return Optional.empty();
		}

	}

	protected final AbstractComponentPilot<C> pilot;

	protected final List<Consumer<Polling<C, ?>>> configurers = new ArrayList<>(2);

	private FailureHandler<C, ?> failureHandler;

	public PollingBuilder(AbstractComponentPilot<C> pilot) {
		this.pilot = pilot;
	}

	public T configure(Consumer<Polling<C, ?>> configurer) {
		configurers.add(configurer);
		return (T) this;
	}

	protected <R> PollingResult<C, R> poll(final Polling<C, R> polling) {
		configurers.forEach(conf -> conf.accept(polling));
		return pilot.processResult(pilot.waitPollingSuccess(polling), PollingResults.identity(),
				((FailureHandler<C, R>) failureHandler));
	}

	public U ifNot() {
		return (U) new Poller<>(this);
	}

	/**
	 * Waits until a condition is applied, throwing a java assertion error in case of
	 * failure
	 */
	public T fail() {
		this.failureHandler = FailureHandlers.throwError();
		return (T) this;
	}

	/**
	 * Waits until a condition is applied, throwing a java assertion error in case of
	 * failure
	 * @param report the text of the error;
	 */
	public T fail(String assertion) {
		this.failureHandler = FailureHandlers.throwError(assertion);
		return (T) this;
	}

	public T fail(ReportFunction<C> reportFunction) {
		configure(polling -> polling.withReportFunction(reportFunction));
		return fail();
	}
	
	/**
	 * Waits until a condition is applied, skipping the error in case of
	 * failure
	 */
	public T eval() {
		this.failureHandler = FailureHandlers.ignoreFailure();
		return (T) this;
	}

	public T eval(Duration timeout) {
		configure(polling -> polling.withTimeout(timeout));
		this.failureHandler = FailureHandlers.ignoreFailure();
		return (T) this;
	}

	public T report(String report) {
		this.failureHandler = FailureHandlers.reportFailure(report);
		return (T) this;
	}


}