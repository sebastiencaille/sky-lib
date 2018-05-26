package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestActor extends IdObject {

	private final List<TestMethod> apis = new ArrayList<>();

	public TestActor(final String id) {
		super(id);
	}

	public List<TestMethod> getApis() {
		return apis;
	}

	@Override
	public String toString() {
		return getId() + ": " + apis.size() + " apis";
	}
}
