package ch.scaille.tcwriter.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.scaille.tcwriter.services.generators.visitors.HumanReadableVisitor;

class HumanReadableVisitorTest {

	@Test
	void testBlockFormatting() {

		assertEquals("Hello world", HumanReadableVisitor.format("Hello %s", List.of("world")));
		assertEquals("Hello world", HumanReadableVisitor.format("Hello// %srld//", List.of("wo")));
		assertEquals("Hello", HumanReadableVisitor.format("Hello// %srld//", List.of("")));
		assertEquals("Hello// rld//", HumanReadableVisitor.format("Hello/\\/ %srld/\\/", List.of("")));
		assertEquals("Hello //world", HumanReadableVisitor.format("Hello %s", List.of("//world")));

	}

	@Test
	void testTextSplit() {
		assertEquals("Hello world", HumanReadableVisitor.format("%s %s", List.of("Hello|world")));
		assertEquals("Hello world", HumanReadableVisitor.format("Hello //x%s//%s", List.of("|world")));
		assertEquals("Hello world", HumanReadableVisitor.format("%s //x%s//world", List.of("Hello|")));
		assertEquals("world", HumanReadableVisitor.format("%s//x%s//world", List.of("|")));
	}

}
