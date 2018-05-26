package ch.skymarshall.tcwriter.generators.model;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import ch.skymarshall.tcwriter.generators.Helper;

public class TestModel {

	private final Map<String, String> descriptions = new HashMap<>();

	private final Map<String, TestRole> roles = new HashMap<>();

	private final Map<String, String> actorToRole = new HashMap<>();

	private final Multimap<String, TestObject> testObjects = MultimapBuilder.hashKeys().arrayListValues().build();

	{
		descriptions.put(IdObject.ID_NOT_SET, "N/A");
	}

	public Map<String, String> getDescriptions() {
		return descriptions;
	}

	public Map<String, TestRole> getRoles() {
		return roles;
	}

	public Map<String, String> getActorToRole() {
		return actorToRole;
	}

	public TestRole getRoleOfActor(final String actor) {
		return roles.get(actorToRole.get(actor));
	}

	public Multimap<String, TestObject> getTestObjects() {
		return testObjects;
	}

	public String descriptionOf(final String key) {
		return descriptions.get(key);
	}

	public TestObject getTestObject(final TestObjectParameter testObjectParameter, final String id) {
		return testObjects.get(testObjectParameter.getType()).stream().filter(tObj -> tObj.getId().equals(id))
				.findFirst().orElseThrow(() -> new IllegalArgumentException(
						"No test object for type " + testObjectParameter.getType() + " and id " + id));
	}

	@Override
	public String toString() {
		return "Model: " + descriptions.size() + " descriptions, " + roles.size() + " actors, " + testObjects.size()
				+ " test Objects";
	}

	public void setActorRole(final String actor, final Class<?> roleClass) {
		actorToRole.put(actor, Helper.roleKey(roleClass));
	}

}
