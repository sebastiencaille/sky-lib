package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestObject extends IdObject {

	public static final TestObject NO_VALUE = new TestObject(IdObject.ID_NOT_SET, "", "");
	private final List<TestObjectParameter> mandatoryParameters = new ArrayList<>();
	private final List<TestObjectParameter> optionalParameters = new ArrayList<>();
	private final String type;
	private final String name;

	public TestObject(final String id, final String name, final String type) {
		super(id);
		this.name = name;
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public boolean isSimpleType() {
		return type.startsWith("java.lang");
	}

	public List<TestObjectParameter> getMandatoryParameters() {
		return mandatoryParameters;
	}

	public List<TestObjectParameter> getOptionalParameters() {
		return optionalParameters;
	}

	@Override
	public String toString() {
		return name + ": " + mandatoryParameters.size() + " mandatory, " + optionalParameters.size() + " optional";
	}

	public String getName() {
		return name;
	}
}
