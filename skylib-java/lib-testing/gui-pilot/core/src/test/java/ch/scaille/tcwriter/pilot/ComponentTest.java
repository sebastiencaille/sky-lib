package ch.scaille.tcwriter.pilot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.tcwriter.pilot.Factories.FailureHandlers;
import ch.scaille.util.helpers.Poller;
import ch.scaille.util.helpers.Poller.DelayFunction;

class ComponentTest {

	public static class TestComponent extends AbstractComponentPilot<TestComponent, Object> {

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
			delays.add(poller.timeTracker.elapsedTimeMs());
			return super.executePolling(poller, polling);
		}

		@Override
		protected Optional<Object> loadGuiComponent() {
			return Optional.of("Hello");
		}

		@Override
		protected boolean canCheck(Object component) {
			return true;
		}

		@Override
		protected boolean canEdit(Object component) {
			return true;
		}

	}

	@Test
	void testDuration() {
		final var pilot = new GuiPilot();
		final var testComponent = new TestComponent(pilot);
		final var waitResult = testComponent.wait(testComponent.satisfies(c -> false), FailureHandlers.reportNotFound("failed"));
		Assertions.assertFalse(waitResult);
		Assertions.assertEquals(6, testComponent.delays.size(), testComponent.delays.toString());

	}

}
