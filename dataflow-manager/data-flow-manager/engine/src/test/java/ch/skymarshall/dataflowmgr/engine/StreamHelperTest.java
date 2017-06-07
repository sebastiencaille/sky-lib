/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
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
