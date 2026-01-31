package ch.scaille.tcwriter.model.dictionary;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.TestObjectDescription;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.services.generators.Helper;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TestDictionary {

    private static final TestObjectDescription NO_ID_DESCRIPTION = new TestObjectDescription("", "");

    private Metadata metadata = new Metadata();

    private String classifier;

    private final Map<String, TestObjectDescription> descriptions = new HashMap<>();

    private final Map<String, TestRole> roles = new HashMap<>();

    private final Map<String, TestActor> actors = new HashMap<>();

    private final Multimap<String, TestParameterFactory> testObjectFactories = MultimapBuilder.hashKeys()
            .arrayListValues().build();

    private final Set<String> selectorTypes = new HashSet<>();

    public TestDictionary() {
        descriptions.put(IdObject.ID_NOT_SET, TestObjectDescription.NOT_SET);
    }

    @JsonCreator
    public TestDictionary(Metadata metadata, String classifier, Map<String, TestObjectDescription> descriptions,
                          Map<String, TestRole> roles, Map<String, TestActor> actors,
                          Multimap<String, TestParameterFactory> testObjectFactories,
                          Set<String> selectorTypes) {

        this.metadata = metadata;
        this.classifier = classifier;
        this.descriptions.putAll(descriptions);
        this.roles.putAll(roles);
        this.actors.putAll(actors);
        this.testObjectFactories.putAll(testObjectFactories);
        this.selectorTypes.addAll(selectorTypes);
    }

    public void addDescription(final IdObject idObject, final TestObjectDescription description) {
        descriptions.put(idObject.getId(), description);
    }

    public TestRole getRole(final Class<?> clazz) {
        return roles.get(Helper.roleKey(clazz));
    }

    public void addActor(final TestActor actor, final TestObjectDescription actorDescription) {
        actors.put(actor.getId(), actor);
        if (actorDescription != null) {
            descriptions.put(actor.getId(), actorDescription);
        } else {
            descriptions.put(actor.getId(), descriptions.get(actor.getRole().getId()));
        }
    }

    public Collection<TestParameterFactory> getParameterFactories(final TestApiParameter paramType) {
        return testObjectFactories.get(paramType.getParameterType());
    }

    public void addSelectorType(final Class<?> type) {
        selectorTypes.add(type.getName());
    }

    public TestObjectDescription descriptionOf(final IdObject idObject) {
        final var description = descriptions.get(idObject.getId());
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
        return selectorTypes.contains(testParameterType.getParameterType());
    }

    public boolean isSelector(final TestParameterValue value) {
        return selectorTypes.contains(value.getValueFactory().getParameterType());
    }
    
}
