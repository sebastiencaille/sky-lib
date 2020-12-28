package ch.skymarshall.util.dao.metadata;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class MetaDataTest {

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
	public void testMetaData() {

		ObjectTest test = new ObjectTest();

		DataObjectMetaData<ObjectTest> metaData = new DataObjectMetaData<>(ObjectTest.class);
		DataObjectManager<ObjectTest> testMetaData = metaData.createAccessorTo(test);

		List<String> attr = metaData.getAttributes().stream().map(Object::toString).sorted()
				.collect(Collectors.toList());
		Assert.assertTrue(attr.toString(), attr.contains("ValA(int)"));
		Assert.assertTrue(attr.toString(), attr.contains("ValB(int, ReadOnly)"));
		Assert.assertTrue(attr.toString(), attr.contains("ValC(int)"));

		testMetaData.getAttributeAccessor("ValA").setValue(123);
		Assert.assertEquals(123, test.getValA());
		Assert.assertEquals(123, testMetaData.getAttributeAccessor("ValA").getValue());
		
		Assert.assertEquals(456, testMetaData.getAttributeAccessor("ValB").getValue());
		
		Assert.assertEquals(NioFieldAttribute.class, metaData.getAttribute("ValC").getClass());
		testMetaData.getAttributeAccessor("ValC").setValue(789);
		Assert.assertEquals(789, test.valC);
		Assert.assertEquals(789, testMetaData.getAttributeAccessor("ValC").getValue());
	}

}
