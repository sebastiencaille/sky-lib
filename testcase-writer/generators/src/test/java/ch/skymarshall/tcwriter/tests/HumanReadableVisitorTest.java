package ch.skymarshall.tcwriter.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import ch.skymarshall.tcwriter.generators.visitors.HumanReadableVisitor;

public class HumanReadableVisitorTest {

	@Test
	public void testBlockFormatting() {

		assertEquals("Hello world", HumanReadableVisitor.format("Hello %s", Arrays.asList("world")));
		assertEquals("Hello world", HumanReadableVisitor.format("Hello// %srld//", Arrays.asList("wo")));
		assertEquals("Hello", HumanReadableVisitor.format("Hello// %srld//", Arrays.asList("")));
		assertEquals("Hello// rld//", HumanReadableVisitor.format("Hello/\\/ %srld/\\/", Arrays.asList("")));

	}

}
