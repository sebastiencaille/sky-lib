package ch.skymarshall.tcwriter.pilot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.skymarshall.util.helpers.Poller.DelayFunction;

class ComponentTest {

	public static class TestComponent extends AbstractComponentPilot<TestComponent, Object> {

		private List<Long> delays = new ArrayList<>();

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
		protected <U> PollingResult<Object, U> callPollingFunction(Polling<Object, U> polling) {
			delays.add(polling.getContext().poller.timeTracker.elapsedTimeMs());
			return super.callPollingFunction(polling);
		}

		@Override
		protected Object loadGuiComponent() {
			return "Hello";
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
		GuiPilot gp = new GuiPilot();
		TestComponent tc = new TestComponent(gp);
		boolean res = tc.wait(tc.satisfies(c -> false), Factories.reportFailure("failed"));
		Assertions.assertFalse(res);
		Assertions.assertEquals(6, tc.delays.size(), tc.delays.toString());

	}

}
