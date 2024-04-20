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
 * Rules:
 * <ul>
 * <li>try... methods require using orFail(...) or satisfied(...)</li>
 * <li>...Poll methods take a Polling as parameter</li>
 * <li>...Apply methods take a Component Consumer as parameter</li>
 * <li>...Satisfy methods take a Predicate as parameter</li>
 * <li>...Assert methods take a Context Consumer as parameter (mostly to access
 * the descriptions)</li>
 * <li>Otherwise, the method should call orFail(...)</li>
 * </ul>
 * 
 * @param <P> the Pilot type
 * @param <C> the Component type
 */
public class PollingBuilder<P extends AbstractComponentPilot<P, C>, C, T extends PollingBuilder<P, C, T, U>, U extends PollingBuilder.Poller<C>> {

	protected interface PollingExecutionProvider<C> {
		<R> FailureHandler<C, R> getPollingExecution();
	}

	public static class Poller<C> {

		protected final PollingBuilder<?, C, ?, ?> builder;

		protected Poller(PollingBuilder<?, C, ?, ?> builder) {
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

	protected final AbstractComponentPilot<P, C> pilot;

	protected final List<Consumer<Polling<C, ?>>> configurers = new ArrayList<>(2);

	protected PollingExecutionProvider<C> pollingExecutionProvider;

	public PollingBuilder(AbstractComponentPilot<P, C> pilot) {
		this.pilot = pilot;
	}

	public T configure(Consumer<Polling<C, ?>> configurer) {
		configurers.add(configurer);
		return (T) this;
	}

	protected <R> PollingResult<C, R> poll(final Polling<C, R> polling) {
		configurers.forEach(conf -> conf.accept(polling));
		return pilot.processResult(pilot.waitPollingSuccess(polling), PollingResults.identity(),
				pollingExecutionProvider.getPollingExecution());
	}

	public U ifNot() {
		return (U) new Poller<>(this);
	}

	/**
	 * Waits until a component is edited, throwing a java assertion error in case of
	 * failure
	 */
	public T fail() {
		this.pollingExecutionProvider = new PollingExecutionProvider<>() {
			@Override
			public <R> FailureHandler<C, R> getPollingExecution() {
				return FailureHandlers.throwError();
			}
		};
		return (T) this;
	}

	public T fail(String report) {
		this.pollingExecutionProvider = new PollingExecutionProvider<>() {
			@Override
			public <R> FailureHandler<C, R> getPollingExecution() {
				return FailureHandlers.throwError(report);
			}
		};
		return (T) this;
	}

	public T ignore() {
		this.pollingExecutionProvider = new PollingExecutionProvider<>() {
			@Override
			public <R> FailureHandler<C, R> getPollingExecution() {
				return FailureHandlers.ignoreFailure();
			}
		};
		return (T) this;
	}
	
	public T ignore(Duration timeout) {
		configure(polling -> polling.withTimeout(timeout));
		this.pollingExecutionProvider = new PollingExecutionProvider<>() {
			@Override
			public <R> FailureHandler<C, R> getPollingExecution() {
				return FailureHandlers.ignoreFailure();
			}
		};
		return (T) this;
	}

	public T report(String report) {
		this.pollingExecutionProvider = new PollingExecutionProvider<>() {
			@Override
			public <R> FailureHandler<C, R> getPollingExecution() {
				return FailureHandlers.reportFailure(report);
			}
		};
		return (T) this;
	}

	public T fail(ReportFunction<C> reportFunction) {
		configurers.add(polling -> polling.withReportFunction(reportFunction));
		return fail();
	}

}