package ch.scaille.tcwriter.pilot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.util.helpers.Poller;
import ch.scaille.util.helpers.Poller.DelayFunction;

class ComponentTest {

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
		protected <U> Optional<PollingResult<Object, U>> executePolling(Poller poller, Polling<Object, U> polling) {
			delays.add(poller.getTimeTracker().elapsedTimeMs());
			return super.executePolling(poller, polling);
		}

		@Override
		protected Optional<Object> loadGuiComponent() {
			return Optional.of(TEST_TEXT);
		}

		@Override
		protected boolean canCheck(PollingContext<Object> ctxt) {
			return true;
		}

	}

	@Test
	void testDuration() {
		final var pilot = new GuiPilot();
		final var testComponent = new TestComponent(pilot);
		final var waitResult = testComponent.polling().ignore().ifNot().satisfied(c -> false);
		Assertions.assertFalse(waitResult);
		Assertions.assertEquals(6, testComponent.delays.size(), testComponent.delays.toString());
	}
	

	@Test
	void testGet() {
		final var pilot = new GuiPilot();
		final var testComponent = new TestComponent(pilot);
		final var successResult = testComponent.polling().fail().ifNot().get(o -> TEST_TEXT);
		Assertions.assertEquals(TEST_TEXT, successResult.get());
		final var failureResult = testComponent.polling().ignore(Duration.ofMillis(10)).ifNot().get(o -> null);
		Assertions.assertTrue(failureResult.isEmpty());
	}

}
