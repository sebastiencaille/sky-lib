package ch.skymarshall.tcwriter.generators.model;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

public class TestModel {

	private final Map<String, String> descriptions = new HashMap<>();

	private final Map<String, TestRole> roles = new HashMap<>();

	private final Map<String, TestActor> actors = new HashMap<>();

	private final Multimap<String, TestParameter> testObjects = MultimapBuilder.hashKeys().arrayListValues().build();

	{
		descriptions.put(IdObject.ID_NOT_SET, "N/A");
	}

	public Map<String, String> getDescriptions() {
		return descriptions;
	}

	public Map<String, TestRole> getRoles() {
		return roles;
	}

	public Map<String, TestActor> getActors() {
		return actors;
	}

	public Multimap<String, TestParameter> getParameterFactories() {
		return testObjects;
	}

	public String descriptionOf(final String key) {
		return descriptions.get(key);
	}

	public String descriptionOf(final IdObject idObject) {
		return descriptionOf(idObject.getId());
	}

	public TestParameter getTestParameter(final String factoryId) {
		return testObjects.values().stream().filter(tObj -> tObj.getId().equals(factoryId)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No test object found with id " + factoryId));
	}

	@Override
	public String toString() {
		return "Model: " + descriptions.size() + " descriptions, " + roles.size() + " actors, " + testObjects.size()
				+ " test Objects";
	}

}
