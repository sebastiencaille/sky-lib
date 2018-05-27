package ch.skymarshall.tcwriter.generators.model;

public class TestActor extends IdObject {

	private final String name;
	private final TestRole role;

	public TestActor(final String id, final String name, final TestRole testRole) {
		super(id);
		this.name = name;
		this.role = testRole;
	}

	public String getName() {
		return name;
	}

	public TestRole getRole() {
		return role;
	}

}
