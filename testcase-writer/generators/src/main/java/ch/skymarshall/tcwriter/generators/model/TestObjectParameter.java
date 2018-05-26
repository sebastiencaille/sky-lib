package ch.skymarshall.tcwriter.generators.model;

public class TestObjectParameter {

	private final String id;
	private final String type;

	public TestObjectParameter(final String id, final String type) {
		this.id = id;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return id + "->" + type;
	}
}
