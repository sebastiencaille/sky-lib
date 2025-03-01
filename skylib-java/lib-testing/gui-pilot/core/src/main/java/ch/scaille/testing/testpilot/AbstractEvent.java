package ch.scaille.testing.testpilot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;

public abstract class AbstractEvent<T extends Enum<T>> {

	public static class EventWaiter<U extends Enum<U>> {
		
		private final List<U> eventHistory = new ArrayList<>();
		private final Semaphore semaphore = new Semaphore(0);
		private final Predicate<List<U>> historyMatcher;

		public EventWaiter(Predicate<List<U>> historyMatcher) {
			this.historyMatcher = historyMatcher;
		}

		public boolean matches(long delay, TimeUnit unit) throws InterruptedException {
			return semaphore.tryAcquire(1, delay, unit);
		}

		public void assertMatches() throws InterruptedException {
			Assertions.assertTrue(matches(5, TimeUnit.SECONDS));
		}

		protected void onEvent(U event) {
			eventHistory.add(event);
			if (checkMatching(eventHistory)) {
				semaphore.release();
			}
		}

		protected boolean checkMatching(List<U> eventHistory) {
			return historyMatcher.test(eventHistory);
		}

		public List<U> received() {
			return eventHistory;
		}

	}

	public abstract EventWaiter<T> expect(Predicate<List<T>> historyTest);

}
