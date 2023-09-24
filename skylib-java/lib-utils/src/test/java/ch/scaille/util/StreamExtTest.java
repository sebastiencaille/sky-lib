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

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.util.helpers.StreamExt;
import ch.scaille.util.helpers.WrongCountException;

class StreamExtTest {

	@Test
	void singleTest() {
		Collections.singletonList(1).stream().collect(StreamExt.single()).orElseThrow(WrongCountException::new);
		try {
			Collections.emptyList().stream().collect(StreamExt.single()).orElseThrow(WrongCountException::new);
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

		final var zeroOrOne1 = Collections.singletonList(1)
				.stream()
				.collect(StreamExt.zeroOrOne())
				.optionalOrThrow(WrongCountException::new);
		assertTrue(zeroOrOne1.isPresent(), () -> "zeroOrOne1.isPresent()");
		assertEquals(Integer.valueOf(1), zeroOrOne1.get());

		final var zeroOrOne2 = Collections.<Integer>emptyList()
				.stream()
				.collect(StreamExt.zeroOrOne())
				.optionalOrThrow(WrongCountException::new);
		assertFalse(zeroOrOne2.isPresent());

		final var exception = Assertions.assertThrows(WrongCountException.class, this::testWith2Values);
		assertEquals("Wrong count: 2", exception.getMessage());
	}

	private Integer testWith2Values() {
		return Arrays.asList(1, 2).stream().collect(StreamExt.zeroOrOne()).orElseThrow(WrongCountException::new);
	}

}
