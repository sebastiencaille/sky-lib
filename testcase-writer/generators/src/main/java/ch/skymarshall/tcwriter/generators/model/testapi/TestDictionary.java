package ch.skymarshall.tcwriter.generators.model.testapi;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import ch.skymarshall.tcwriter.generators.Helper;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.tc.TestObjectDescription;

public class TestDictionary implements Serializable {

	private static final TestObjectDescription NO_ID_DESCRIPTION = new TestObjectDescription("", "");

	private final Map<String, TestObjectDescription> descriptions = new HashMap<>();

	private final Map<String, TestRole> roles = new HashMap<>();

	private final Map<String, TestActor> actors = new HashMap<>();

	private final Multimap<String, TestParameterFactory> testObjectFactories = MultimapBuilder.hashKeys()
			.arrayListValues().build();

	private final Set<String> selectorTypes = new HashSet<>();

	public TestDictionary() {
		descriptions.put(IdObject.ID_NOT_SET, TestObjectDescription.NOT_SET);
	}

	public Map<String, TestObjectDescription> getDescriptions() {
		return descriptions;
	}

	public void addDescription(final IdObject idObject, final TestObjectDescription description) {
		descriptions.put(idObject.getId(), description);
	}

	public Map<String, TestRole> getRoles() {
		return roles;
	}

	public TestRole getRole(final Class<?> clazz) {
		return roles.get(Helper.roleKey(clazz));
	}

	public Map<String, TestActor> getActors() {
		return actors;
	}

	public void addActor(final TestActor actor, final TestObjectDescription actorDescription) {
		actors.put(actor.getId(), actor);
		if (actorDescription != null) {
			descriptions.put(actor.getId(), actorDescription);
		} else {
			descriptions.put(actor.getId(), descriptions.get(actor.getRole().getId()));
		}
	}

	public Multimap<String, TestParameterFactory> getParameterFactories() {
		return testObjectFactories;
	}

	public Collection<TestParameterFactory> getParameterFactories(final TestApiParameter paramType) {
		return testObjectFactories.get(paramType.getType());
	}

	public void addNavigationType(final Class<?> type) {
		selectorTypes.add(type.getName());
	}

	public TestObjectDescription descriptionOf(final IdObject idObject) {
		final TestObjectDescription description = descriptions.get(idObject.getId());
		if (description == null) {
			return NO_ID_DESCRIPTION;
		}
		return description;
	}

	public TestParameterFactory getTestParameterFactory(final String factoryId) {
		return testObjectFactories.values().stream().filter(tObj -> tObj.getId().equals(factoryId)).findFirst()
				.orElseThrow(
						() -> new IllegalArgumentException("No test parameter factory found with id " + factoryId));
	}

	@Override
	public String toString() {
		return "Model: " + descriptions.size() + " descriptions, " + roles.size() + " actors, "
				+ testObjectFactories.size() + " test Objects";
	}

	public boolean isSelector(final TestApiParameter testParameterType) {
		return selectorTypes.contains(testParameterType.getType());
	}

	public boolean isSelector(final TestParameterValue value) {
		return selectorTypes.contains(value.getValueFactory().getType());
	}

}
