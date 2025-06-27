package ch.scaille.example.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.scaille.javabeans.Converters;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.properties.IntProperty;
import ch.scaille.javabeans.properties.LongProperty;

class PropertiesTest {

	record P1P2(IntProperty p1, LongProperty p2) {

	}

	@Test
	void testContext() {

		final var accumlator = new long[] {0}; 
		final var changeSupport = PropertyChangeSupportController.mainGroup(this);
		final var p1 = new IntProperty("p1", changeSupport);
		final var p2 = new LongProperty("p2", changeSupport);

		p1.contextualChain(p2).bind(Converters.listen((v, k) -> v + k.getValue())).listen(v -> accumlator[0] = v);
		
		changeSupport.flushChanges();
		
		p1.setValue(this, 1);
		assertEquals(1, accumlator[0]);

		p2.setValue(this, 1000);
		assertEquals(1001, accumlator[0]);
	}
	
	private record Context(LongProperty ctxt) {
		
	}
	
	@Test
	void testRecordContext() {

		final var accumlator = new long[] {0}; 
		final var changeSupport = PropertyChangeSupportController.mainGroup(this);
		final var p1 = new IntProperty("p1", changeSupport);
		final var p2 = new LongProperty("p2", changeSupport);

		p1.contextualChain(new Context(p2)).bind(Converters.listen((v, k) -> v + k.ctxt.getValue())).listen(v -> accumlator[0] = v);
		
		changeSupport.flushChanges();
		
		p1.setValue(this, 1);
		assertEquals(1, accumlator[0]);

		p2.setValue(this, 1000);
		assertEquals(1001, accumlator[0]);
	}
}

