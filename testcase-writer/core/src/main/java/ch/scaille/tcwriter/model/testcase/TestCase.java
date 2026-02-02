package ch.scaille.tcwriter.model.testcase;

import java.util.*;

import ch.scaille.tcwriter.mappers.Default;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.TestObjectDescription;
import ch.scaille.tcwriter.model.dictionary.TestApiParameter;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Getter
@Setter
public class TestCase {

    protected Metadata metadata = new Metadata();

    @JsonIgnore
    protected TestDictionary dictionary = TestDictionary.NOT_SET;

    @Nullable
    protected String preferredDictionary;

    protected final List<TestStep> steps = new ArrayList<>();

    protected String pkgAndClassName;

    protected final Multimap<String, TestReference> dynamicReferences = MultimapBuilder.hashKeys().arrayListValues()
            .build();

    protected final Map<String, TestObjectDescription> dynamicDescriptions = new HashMap<>();

    public TestCase(final String pkgAndClassName, final TestDictionary testDictionary) {
        this.pkgAndClassName = pkgAndClassName;
        this.dictionary = testDictionary;
    }

    @Default
    @JsonCreator
    public TestCase(Metadata metadata, String preferredDictionary, List<TestStep> steps, final String pkgAndClassName,
                    @Nullable Multimap<String, TestReference> dynamicReferences,
                    @Nullable Map<String, TestObjectDescription> dynamicDescriptions) {
        this.metadata = Objects.requireNonNull(metadata, "Metadata must not be null");
        this.preferredDictionary = preferredDictionary;
        this.pkgAndClassName = pkgAndClassName;
        this.steps.addAll(steps);
        if (dynamicReferences != null) {
            this.dynamicReferences.putAll(dynamicReferences);
        }
        if (dynamicDescriptions != null) {
            this.dynamicDescriptions.putAll(dynamicDescriptions);
        }
    }

    public void addStep(final TestStep step) {
        steps.add(step);
    }

    public String getPackage() {
        return pkgAndClassName.substring(0, pkgAndClassName.lastIndexOf('.'));
    }

    public String getName() {
        return pkgAndClassName.substring(pkgAndClassName.lastIndexOf('.') + 1);
    }

    public void publishReference(final TestReference reference) {
        dynamicReferences.put(reference.getParameterType(), reference);
        dynamicDescriptions.put(reference.getId(), reference.toDescription());
    }

    public TestObjectDescription descriptionOf(final IdObject idObject) {
        return descriptionOf(idObject.getId());
    }

    public TestObjectDescription descriptionOf(final String id) {
        final var modelDescr = Objects.requireNonNull(dictionary, "Test dictionary must be set").getDescriptions().get(id);
        if (modelDescr != null) {
            return modelDescr;
        }
        final var dynamicDescr = dynamicDescriptions.get(id);
        if (dynamicDescr != null) {
            return dynamicDescr;
        }
        return new TestObjectDescription(id, "");
    }

    public Optional<TestReference> getReference(final String reference) {
        return dynamicReferences.values().stream().filter(ref -> ref.getName().equals(reference)).findFirst();
    }

    public Collection<TestReference> getReferences(final String returnType) {
        return dynamicReferences.get(returnType);
    }

    public Collection<TestReference> getSuitableReferences(final TestApiParameter param) {
        return getReferences(param.getParameterType());
    }

    public void fixOrdinals() {
        for (int i = 0; i < steps.size(); i++) {
            steps.get(i).setOrdinal(i + 1);
        }
    }

    public TestApiParameter getTestParameter(final String apiParameterId) {
        if (apiParameterId.isEmpty()) {
            return TestApiParameter.NO_PARAMETER;
        }
        return Objects.requireNonNull(dictionary, "Test dictionary must be set")
                .getRoles().values().stream()
                .flatMap(s -> s.getActions().stream())
                .flatMap(a -> a.getParameters().stream()).filter(p -> p.getId().equals(apiParameterId)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Unable to find " + apiParameterId));
    }

    @Override
    public String toString() {
        return "Steps: " + steps.size();
    }

}
