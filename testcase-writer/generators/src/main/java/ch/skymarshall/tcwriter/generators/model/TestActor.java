package ch.skymarshall.tcwriter.generators.model;

public class TestActor extends IdObject {

	public static final TestActor NOT_SET = new TestActor(IdObject.ID_NOT_SET, "", TestRole.NOT_SET);
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
