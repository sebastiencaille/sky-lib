package ch.scaille.tcwriter.model.dictionary;

import java.util.ArrayList;
import java.util.List;

import ch.scaille.tcwriter.model.NamedObject;

public class TestRole extends NamedObject {

	public static final TestRole NOT_SET = new TestRole(ID_NOT_SET, "");
	private final List<TestAction> actions = new ArrayList<>();

	protected TestRole() {
		super(null, null);
	}

	public TestRole(final String id, String name) {
		super(id, name);
	}

	public List<TestAction> getActions() {
		return actions;
	}

	@Override
	public String toString() {
		return getId() + ": " + actions.size() + " apis";
	}

	public TestAction getAction(final String newId) {
		return actions.stream().filter(api -> api.getId().equals(newId)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No api with id " + newId));
	}
}
