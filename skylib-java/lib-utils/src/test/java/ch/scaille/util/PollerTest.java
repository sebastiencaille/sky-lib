package ch.scaille.util;

import java.time.Duration;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.util.helpers.Poller;

class PollerTest {

	@Test
	void testDuration() {

		final var delays = new ArrayList<Long>();

		final var poller = new Poller(Duration.ofMillis(500), Duration.ofMillis(100), p -> Duration.ofMillis(100));
		poller.run(p -> {
			delays.add(p.timeTracker.elapsedTimeMs());
			return null;
		}, p -> false);
		System.out.println(delays);

		Assertions.assertEquals(5, poller.getExecutionCount(), "Exec count");
		Assertions.assertEquals(5, delays.size(), delays.toString());
	}

}
