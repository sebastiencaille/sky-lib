package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestRole extends IdObject {

	private final List<TestMethod> apis = new ArrayList<>();

	public TestRole(final String id) {
		super(id);
	}

	public List<TestMethod> getApis() {
		return apis;
	}

	@Override
	public String toString() {
		return getId() + ": " + apis.size() + " apis";
	}

	public TestMethod getApi(final String newId) {
		return apis.stream().filter(api -> api.getId().equals(newId)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No api with id " + newId));
	}
}
