package ch.skymarshall.tcwriter.generators.model.testapi;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.NamedObject;

public class TestActor extends NamedObject {

	public static final TestActor NOT_SET = new TestActor(IdObject.ID_NOT_SET, "", TestRole.NOT_SET);
	private final TestRole role;

	protected TestActor() {
		super(null, null);
		role = null;
	}

	public TestActor(final String id, final String name, final TestRole testRole) {
		super(id, name);
		this.role = testRole;
	}

	public TestRole getRole() {
		return role;
	}

}
