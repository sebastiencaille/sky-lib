package ch.skymarshall.tcwriter.generators.model;

public class TestObjectParameter extends IdObject {

	private final String type;
	private final String name;

	public TestObjectParameter(final String id, final String name, final String type) {
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
		return name + "->" + type;
	}
}
