package ch.skymarshall.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.skymarshall.util.helpers.Poller;

class PollerTest {

	@Test
	void testDuration() {

		List<Long> delays = new ArrayList<>();

		Poller poller = new Poller(Duration.ofMillis(500), Duration.ofMillis(100), p -> Duration.ofMillis(100));
		poller.run(() -> {
			delays.add(poller.timeTracker.elapsedTimeMs());
			return null;
		}, p -> false);
		System.out.println(delays);
		
		Assertions.assertEquals(5, poller.getExecutionCount(), "Exec count");
		Assertions.assertEquals(5, delays.size(), delays.toString());
	}

}
