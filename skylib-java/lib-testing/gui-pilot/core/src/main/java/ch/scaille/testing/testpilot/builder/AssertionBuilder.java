package ch.scaille.testing.testpilot.builder;

import ch.scaille.testing.testpilot.AbstractComponentPilot;
import ch.scaille.testing.testpilot.PolledComponent;
import ch.scaille.testing.testpilot.Polling;
import ch.scaille.testing.testpilot.PollingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import ch.scaille.testing.testpilot.PilotReport.PilotReportBuilder;
import ch.scaille.testing.testpilot.factories.FailureHandlers;
import ch.scaille.testing.testpilot.factories.FailureHandlers.FailureHandler;
import ch.scaille.testing.testpilot.factories.PollingResults;
import org.assertj.core.api.ObjectAssert;
import org.jspecify.annotations.Nullable;

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
 * @param <P> the Poller (sub)type
 */

public class AssertionBuilder<C,
	T extends AssertionBuilder<C, T, P, V>,
	P extends AssertionChain<C>,
	V extends Configurer<C, V>> {


	protected final AbstractComponentPilot<C> pilot;

	protected final List<Consumer<Polling.PollingBuilder<C, ?>>> configurers = new ArrayList<>(2);

	@Nullable
	private FailureHandler<C, ?> failureHandler;


	public class Unless {

		/**
		 * Waits until a condition is applied
		 */
		public P unless() {
			return createPoller();
		}

	}

	public class That {

		/**
		 * Waits until a condition is applied
		 */
		public P that() {
			return createPoller();
		}

	}
	
	public AssertionBuilder(AbstractComponentPilot<C> pilot) {
		this.pilot = pilot;
	}


	protected P createPoller() {
		return (P) new AssertionChain<>(this);
	}

	protected V createConfigurer() {
		return (V) new DefaultConfigurer<>(this);
	}

	final AtomicReference<@Nullable String> injectedDescriptionHolder = new AtomicReference<>(null);

	/**
	 * Executes the polling
	 */
	protected <R extends @Nullable Object> PollingResult<C, R> poll(final Polling.PollingBuilder<C, R> pollingBuilder) {
		configurers.forEach(conf -> conf.accept(pollingBuilder));
		final var polling = pollingBuilder.build();
		final var actualReportFunction = polling.initializeFrom(pilot).getReportBuilder();
		polling.setReporting(new PilotReportBuilder<>() {
			private String elementName = null;
			@Override
			public void prepare(PolledComponent<C> context) {
				elementName = context.description();
				if (actualReportFunction != null) {
					actualReportFunction.prepare(context);
				}
			}

			@Override
			public @Nullable String build() {
				final var aspectjDescription = injectedDescriptionHolder.getAndSet(null);
				if (aspectjDescription != null) {
					return elementName + ':' + aspectjDescription;
				}
				return actualReportFunction.build();
			}
		});
		try {
			return pilot.processResult(pilot.waitPollingSuccess(polling),
					PollingResults.identity(),
					(FailureHandler<C, R>) Objects.requireNonNull(failureHandler, "Failure handler was not set"));
		} finally {
			reset();
		}
	}

	public void reset() {
		configurers.clear();
	}


	public T with(Consumer<V> configuration) {
		configuration.accept(createConfigurer());
		return (T)this;
	}
	
	public T withConfig(Consumer<Polling.PollingBuilder<C, ?>> configurer) {
		configurers.add(configurer);
		return (T)this;
	}
	
	/**
	 * Waits until a condition is applied, throwing a java assertion error in case
	 * of failure
	 */
	public Unless fail() {
		this.failureHandler = FailureHandlers.throwError();
		return new Unless();
	}

	/**
	 * Waits until a condition is applied, throwing a java assertion error in case
	 * of failure
	 * @param assertion the text of the assertion
	 */
	public Unless failWith(String assertion) {
		this.failureHandler = FailureHandlers.throwError(assertion);
		return new Unless();
	}

	public Unless failWith(PilotReportBuilder<C> report) {
		return withConfig(polling -> polling.reporting(report)).fail();
	}

	public P failUnless() {
		return fail().unless();
	}

	public AssertionResult<C> assertThat(Consumer<ObjectAssert<C>> assertion) {
		return failUnless().assertThat(assertion);
	}

	/**
	 * Waits until a condition is applied, ignoring the error in case of failure
	 */
	public P evaluateThat() {
		this.failureHandler = FailureHandlers.ignoreFailure();
		return createPoller();
	}

	/**
	 * Reports the failure but do not fail the test
	 */
	public That evaluateWithReport(String report) {
		this.failureHandler = FailureHandlers.reportFailure(report);
		return new That();
	}

}