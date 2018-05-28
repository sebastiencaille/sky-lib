package ch.skymarshall.tcwriter.generators.model;

public class TestParameterType extends IdObject {
	public static final TestParameterType NO_VALUE = new TestParameterType(IdObject.ID_NOT_SET, "", "");

	private final String type;
	private final String name;

	public TestParameterType(final String id, final String name, final String type) {
		super(id);
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return getName() + ": " + type;
	}
}
