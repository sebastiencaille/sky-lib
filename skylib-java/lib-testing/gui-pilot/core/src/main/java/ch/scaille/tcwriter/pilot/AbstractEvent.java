package ch.scaille.tcwriter.pilot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public abstract class AbstractEvent<T extends Enum<T>> {
	
	public static class EventWaiter<U extends Enum<U>> {
		private final List<U> eventHistory = new ArrayList<>();
		private final Semaphore semaphore = new Semaphore(0);
		
		public boolean matches() {
			return semaphore.tryAcquire(1);
		}
		
		protected void onEvent(U event) {
			eventHistory.add(event);
			if (checkMatching(eventHistory)) {
				semaphore.release();
			}
		}

		private boolean checkMatching(List<U> eventHistory) {
			return true;
		}
		
	}
	
	public abstract EventWaiter<T> expect();
	
	
}
