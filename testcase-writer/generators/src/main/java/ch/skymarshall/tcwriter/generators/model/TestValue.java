package ch.skymarshall.tcwriter.generators.model;

import java.util.HashMap;
import java.util.Map;

/** A value in the test case */
public class TestValue extends IdObject {

	public static final TestValue NO_VALUE = new TestValue("", TestObject.NO_VALUE);

	private final TestObject testObject;
	private final Map<String, TestValue> testObjectParameters = new HashMap<>();
	private String simpleValue;

	public TestValue(final String id, final TestObject testObject) {
		super(id);
		this.testObject = testObject;
	}

	public TestObject getTestObject() {
		return testObject;
	}

	public Map<String, TestValue> getTestObjectParameters() {
		return testObjectParameters;
	}

	public String getSimpleValue() {
		return simpleValue;
	}

	public TestValue setSimpleValue(final String simpleValue) {
		this.simpleValue = simpleValue;
		return this;
	}

}
