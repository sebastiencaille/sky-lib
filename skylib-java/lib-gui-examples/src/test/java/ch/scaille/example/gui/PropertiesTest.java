package ch.scaille.example.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.properties.IntProperty;
import ch.scaille.javabeans.properties.LongProperty;
import ch.scaille.javabeans.properties.PropertiesRecord;

class PropertiesTest {

	record P1P2(IntProperty p1, LongProperty p2) {

	}

	@Test
	void testRecord() {

		final var accumulator = new long[] {0}; 
		final var changeSupport = PropertyChangeSupportController.mainGroup(this);
		final var p1 = new IntProperty("p1", changeSupport);
		final var p2 = new LongProperty("p2", changeSupport);

		PropertiesRecord.of(new P1P2(p1, p2), changeSupport).listen(p1p2 -> accumulator[0] = p1p2.p1().getValue() + p1p2.p2().getValue());
		
		changeSupport.flushChanges();
		
		p1.setValue(this, 1);
		assertEquals(1, accumulator[0]);

		p2.setValue(this, 1000);
		assertEquals(1001, accumulator[0]);
	}

}

