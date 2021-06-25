package ch.skymarshall.util.dao.metadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class MetaDataTest {

	private static class ObjectTest {

		private int valA;
		private int valB = 456;
		public int valC;
		public final int valD = 123;

		public int getValA() {
			return valA;
		}

		public void setValA(int valA) {
			this.valA = valA;
		}

		public int getValB() {
			return valB;
		}

	}

	@Test
	void testMetaData() {

		ObjectTest test = new ObjectTest();

		DataObjectMetaData<ObjectTest> metaData = new DataObjectMetaData<>(ObjectTest.class);
		DataObjectManager<ObjectTest> testMetaData = metaData.createAccessorTo(test);

		List<String> attr = metaData.getAttributes().stream().map(Object::toString).sorted()
				.collect(Collectors.toList());
		assertTrue(attr.contains("ValA(int)"), () -> attr.toString());
		assertTrue(attr.contains("ValB(int, ReadOnly)"), () -> attr.toString());
		assertTrue(attr.contains("ValC(int)"), () -> attr.toString());

		testMetaData.getAttributeAccessor("ValA").setValue(123);
		assertEquals(123, test.getValA());
		assertEquals(123, testMetaData.getAttributeAccessor("ValA").getValue());

		assertEquals(456, testMetaData.getAttributeAccessor("ValB").getValue());

		assertEquals(NioFieldAttribute.class, metaData.getAttribute("ValC").getClass());
		testMetaData.getAttributeAccessor("ValC").setValue(789);
		assertEquals(789, test.valC);
		assertEquals(789, testMetaData.getAttributeAccessor("ValC").getValue());
	}

}
