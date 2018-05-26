package ch.skymarshall.tcwriter.generators.model;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

public class TestModel {

	private final Map<String, String> descriptions = new HashMap<>();

	private final Map<String, TestActor> actors = new HashMap<>();

	private final Multimap<String, TestObject> testObjects = MultimapBuilder.hashKeys().arrayListValues().build();

	public Map<String, String> getDescriptions() {
		return descriptions;
	}

	public Map<String, TestActor> getActors() {
		return actors;
	}

	public Multimap<String, TestObject> getTestObjects() {
		return testObjects;
	}

	@Override
	public String toString() {
		return "Model: " + descriptions.size() + " descriptions, " + actors.size() + " actors, " + testObjects.size()
				+ " test Objects";
	}
}
