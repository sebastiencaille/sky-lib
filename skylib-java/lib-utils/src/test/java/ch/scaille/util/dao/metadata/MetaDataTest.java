package ch.scaille.util.dao.metadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class MetaDataTest {

	private static class ObjectTest {

		private int valA;
		private final int valB = 456;
		public int valC;
		@SuppressWarnings("unused")
		public final int valD = 123;

		public int getValA() {
			return valA;
		}

		@SuppressWarnings("unused")
		public void setValA(int valA) {
			this.valA = valA;
		}

		@SuppressWarnings("unused")
		public int getValB() {
			return valB;
		}

	}

	@Test
	void testMetaData() {

		final var test = new ObjectTest();

		final var metaData = new DataObjectMetaData<>(ObjectTest.class);
		final var testMetaData = metaData.createAccessorTo(test);

		final var attr = metaData.getAttributes().stream().map(Object::toString).sorted().collect(Collectors.toList());
		assertTrue(attr.contains("ValA(int)"), attr::toString);
		assertTrue(attr.contains("ValB(int, ReadOnly)"), attr::toString);
		assertTrue(attr.contains("ValC(int)"), attr::toString);

		final var valAAccessor = testMetaData.getAttributeAccessor("ValA");
		final var valBAccessor = testMetaData.getAttributeAccessor("ValB");
		final var valCAccessor = testMetaData.getAttributeAccessor("ValC");
		
		valAAccessor.setValue(123);
		assertEquals(123, valAAccessor.getValue());
		assertEquals(456, valBAccessor.getValue());
		assertEquals(123, test.getValA());
		assertEquals(456, test.getValB());

		assertEquals(NioFieldAttribute.class, metaData.getAttribute("ValC").getClass());
		valCAccessor.setValue(789);
		assertEquals(789, valCAccessor.getValue());
		assertEquals(789, test.valC);
	}

}
