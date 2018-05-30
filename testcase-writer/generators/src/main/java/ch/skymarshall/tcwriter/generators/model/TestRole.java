package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestRole extends IdObject {

	public static final TestRole NOT_SET = new TestRole(ID_NOT_SET);
	private final List<TestAction> apis = new ArrayList<>();

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
