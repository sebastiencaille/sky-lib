/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.scaille.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.util.helpers.StreamExt;
import ch.scaille.util.helpers.WrongCountException;

class StreamExtTest {

	@Test
	void singleTest() {
		Arrays.asList(1).stream().collect(StreamExt.single()).orElseThrow(WrongCountException::new);
		try {
			Arrays.asList().stream().collect(StreamExt.single()).orElseThrow(WrongCountException::new);
		} catch (final Exception e) {
			assertEquals("Wrong count: 0", e.getMessage());
		}
		try {
			Arrays.asList(1, 2).stream().collect(StreamExt.single()).orElseThrow(WrongCountException::new);
		} catch (final Exception e) {
			assertEquals("Wrong count: 2", e.getMessage());
		}
	}

	@Test
	void zeroOrOneTest() {

		final Optional<Integer> zeroOrOne1 = Arrays.asList(1).stream().collect(StreamExt.zeroOrOne())
				.optionalOrThrow(WrongCountException::new);
		assertTrue(zeroOrOne1.isPresent(), () -> "zeroOrOne1.isPresent()");
		assertEquals(Integer.valueOf(1), zeroOrOne1.get());

		final Optional<Integer> zeroOrOne2 = Collections.<Integer>emptyList().stream().collect(StreamExt.zeroOrOne())
				.optionalOrThrow(WrongCountException::new);
		assertFalse(zeroOrOne2.isPresent());

		WrongCountException e = Assertions.assertThrows(WrongCountException.class, () -> testWith2Values());
		assertEquals("Wrong count: 2", e.getMessage());
	}

	private Integer testWith2Values() {
		return Arrays.asList(1, 2).stream().collect(StreamExt.zeroOrOne()).orElseThrow(WrongCountException::new);
	}

	@Test
	void testIterator() throws IOException {
		URL A = new URL("file:/a");
		URL B = new URL("file:/b");
		URL C = new URL("file:/c");
		String A1 = "a1";
		String A2 = "a2";
		String B1 = "b1";
		String B2 = "b2";
		String C1 = "c1";
		String C2 = "c2";

		List<String> result = StreamExt.multiCollection(Arrays.asList(A, B, C), r -> {
			if (r == A) {
				return  Arrays.asList(A1, A2).iterator();
			} else if (r == B) {
				return Arrays.asList(B1, B2).iterator();
			} else if (r == C) {
				return Arrays.asList(C1, C2).iterator();
			}
			return null;
		}).collect(Collectors.toList());
		Assertions.assertEquals(Arrays.asList(A1, A2, B1, B2, C1, C2), result);

	}

}
