package ch.skymarshall.tcwriter.generators.model;

import java.util.Map;

public class TestValue {
	private final TestObject testObject;
	private Map<String, Object> testObjectValues;

	public TestValue(final TestObject testObject) {
		this.testObject = testObject;
	}

	public TestObject getTestObject() {
		return testObject;
	}

}
