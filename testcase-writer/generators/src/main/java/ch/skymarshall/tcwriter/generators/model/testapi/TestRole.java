package ch.skymarshall.tcwriter.generators.model.testapi;

import java.util.ArrayList;
import java.util.List;

import ch.skymarshall.tcwriter.generators.model.IdObject;

public class TestRole extends IdObject {

	public static final TestRole NOT_SET = new TestRole(ID_NOT_SET);
	private final List<TestAction> apis = new ArrayList<>();

	protected TestRole() {
		super(null);
	}

	public TestRole(final String id) {
		super(id);
	}

	public List<TestAction> getApis() {
		return apis;
	}

	@Override
	public String toString() {
		return getId() + ": " + apis.size() + " apis";
	}

	public TestAction getApi(final String newId) {
		return apis.stream().filter(api -> api.getId().equals(newId)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No api with id " + newId));
	}
}
