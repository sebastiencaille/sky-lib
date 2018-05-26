package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestActor {

	private final String id;

	private final List<TestMethod> apis = new ArrayList<>();

	public TestActor(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public List<TestMethod> getApis() {
		return apis;
	}

	@Override
	public String toString() {
		return id + ": " + apis.size() + " apis";
	}
}
