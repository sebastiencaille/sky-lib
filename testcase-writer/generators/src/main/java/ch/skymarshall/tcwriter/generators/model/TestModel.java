package ch.skymarshall.tcwriter.generators.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

public class TestModel {

	private final Map<String, String> descriptions = new HashMap<>();

	private final Map<String, TestRole> roles = new HashMap<>();

	private final Map<String, TestActor> actors = new HashMap<>();

	private final Multimap<String, TestParameter> testObjects = MultimapBuilder.hashKeys().arrayListValues().build();

	private final Set<String> navigationTypes = new HashSet<>();

	public TestModel() {
		descriptions.put(IdObject.ID_NOT_SET, "N/A");
	}

	Map<String, String> getDescriptions() {
		return descriptions;
	}

	public void addDescription(final IdObject idObject, final String description) {
		descriptions.put(idObject.getId(), description);
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

	public void addNavigationType(final Class<?> type) {
		navigationTypes.add(type.getName());
	}

	public String descriptionOf(final IdObject idObject) {
		return descriptions.get(idObject.getId());
	}

	public TestParameter getTestParameterFactory(final String factoryId) {
		return testObjects.values().stream().filter(tObj -> tObj.getId().equals(factoryId)).findFirst().orElseThrow(
				() -> new IllegalArgumentException("No test parameter factory found with id " + factoryId));
	}

	@Override
	public String toString() {
		return "Model: " + descriptions.size() + " descriptions, " + roles.size() + " actors, " + testObjects.size()
				+ " test Objects";
	}

	public boolean isNavigation(final TestParameterType testParameterType) {
		return navigationTypes.contains(testParameterType.getType());
	}

}
