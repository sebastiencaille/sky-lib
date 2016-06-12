package ch.skymarshall.dataflowmgr.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public class StreamHelperTest extends Assert {

	@Test
	public void zeroOrOneTest() throws Exception {

		final Optional<Integer> zeroOrOne1 = Arrays.asList(1).stream().collect(StreamHelper.zeroOrOne())
				.orElseThrow(cnt -> new Exception("Fail"));
		assertTrue(zeroOrOne1.isPresent());
		assertEquals(Integer.valueOf(1), zeroOrOne1.get());

		final Optional<Integer> zeroOrOne2 = Collections.<Integer>emptyList().stream().collect(StreamHelper.zeroOrOne())
				.orElseThrow(cnt -> new Exception("Fail"));
		assertFalse(zeroOrOne2.isPresent());

		try {
			Arrays.asList(1, 2).stream().collect(StreamHelper.zeroOrOne())
					.orElseThrow(cnt -> new Exception("Fail: " + cnt));
			fail("Call should have failed");
		} catch (final Exception e) {
			assertEquals("Fail: 2", e.getMessage());
		}
	}

}
