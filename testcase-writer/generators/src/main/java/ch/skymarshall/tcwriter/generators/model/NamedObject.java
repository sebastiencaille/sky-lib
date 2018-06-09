package ch.skymarshall.tcwriter.generators.model;

public class NamedObject extends IdObject {

	private String name;

	public NamedObject(final String id, final String name) {
		super(id);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	protected void setName(final String name) {
		this.name = name;
	}

}
