package ch.scaille.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.util.helpers.StreamExt;
import ch.scaille.util.helpers.WrongCountException;

class StreamExtTest {

	@Test
	void singleTest() {
		Stream.of(1).collect(StreamExt.single()).orElseThrow(WrongCountException::new);
		try {
			Stream.empty().collect(StreamExt.single()).orElseThrow(WrongCountException::new);
		} catch (final Exception e) {
			assertEquals("Wrong count: 0", e.getMessage());
		}
		try {
			Stream.of(1, 2).collect(StreamExt.single()).orElseThrow(WrongCountException::new);
		} catch (final Exception e) {
			assertEquals("Wrong count: 2", e.getMessage());
		}
	}

	@Test
	void zeroOrOneTest() {

		final var zeroOrOne1 = Stream.of(1)
				.collect(StreamExt.zeroOrOne())
				.optionalOrThrow(WrongCountException::new);
		assertTrue(zeroOrOne1.isPresent(), "zeroOrOne1.isPresent()");
		assertEquals(Integer.valueOf(1), zeroOrOne1.get());

		final var zeroOrOne2 = Stream.empty()
				.collect(StreamExt.zeroOrOne())
				.optionalOrThrow(WrongCountException::new);
		assertFalse(zeroOrOne2.isPresent());

		final var exception = Assertions.assertThrows(WrongCountException.class, this::testWith2Values);
		assertEquals("Wrong count: 2", exception.getMessage());
	}

	private Integer testWith2Values() {
		return Stream.of(1, 2).collect(StreamExt.zeroOrOne()).orElseThrow(WrongCountException::new);
	}

}
