package ch.skymarshall.dataflowmgr.generator.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TemplateTest {

	@Test
	public void simpleTest() {
		final Transformer template = new Transformer();
		template.name = "Test";
		template.regexp = "(.*\\.)([^\\.]*)";
		template.variables = new String[] { "a", "b" };
		template.output = "$a-$b";
		template.init();
		assertEquals("c.-d", template.transform("c.d"));
	}

}
