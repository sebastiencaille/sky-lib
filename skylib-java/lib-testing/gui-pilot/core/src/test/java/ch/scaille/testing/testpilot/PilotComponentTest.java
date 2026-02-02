package ch.scaille.testing.testpilot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.scaille.util.helpers.DelayFunction;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.util.helpers.Poller;

@NullMarked
class PilotComponentTest {

	private static final String TEST_TEXT = "Hello";

	public static class TestComponent extends AbstractComponentPilot<Object> {

		private final List<Long> delays = new ArrayList<>();

		protected TestComponent(GuiPilot pilot) {
			super(pilot);
		}

		@Override
		protected Duration getDefaultPollingTimeout() {
			return Duration.ofMillis(500);
		}

		@Override
		protected DelayFunction getDefaultPollingDelayFunction() {
			return t -> Duration.ofMillis(100);
		}

		@Override
		protected <U> Optional<PollingResult<Object, U>> executePolling(Poller poller, Polling<Object, U>.InitializedPolling polling) {
			delays.add(poller.getTimeTracker().elapsedTimeMs());
			return super.executePolling(poller, polling);
		}

		@Override
		protected Optional<Object> loadGuiComponent() {
			return Optional.of(TEST_TEXT);
		}

		@Override
		public boolean canCheck(PolledComponent<Object> ctxt) {
			return true;
		}

	}

	@Test
	void testDuration() {
		final var pilot = new GuiPilot();
		final var testComponent = new TestComponent(pilot);
		final var poller = new PollingBuilder<>(testComponent);
		final var waitResult = poller.evaluateThat().satisfied(c -> false);
		Assertions.assertFalse(waitResult);
		Assertions.assertEquals(6, testComponent.delays.size(), testComponent.delays.toString());
	}
	

	@Test
	void testGet() {
		final var pilot = new GuiPilot();
		final var testComponent = new TestComponent(pilot);
		final var poller = new PollingBuilder<>(testComponent);
		final var successResult = poller.failUnless().get(o -> TEST_TEXT);
		Assertions.assertEquals(TEST_TEXT, successResult.orElseThrow());
		final var failureResult = poller
				.with(cfg -> cfg.timingOutAfter(Duration.ofMillis(10)))
				.evaluateThat().get(o -> null);
		Assertions.assertTrue(failureResult.isEmpty());
	}

}
