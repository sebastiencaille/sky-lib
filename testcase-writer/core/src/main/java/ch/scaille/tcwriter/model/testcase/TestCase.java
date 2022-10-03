package ch.scaille.tcwriter.model.testcase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.TestObjectDescription;
import ch.scaille.tcwriter.model.dictionary.TestApiParameter;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;

public class TestCase {

	protected Metadata metadata = new Metadata();
	
	protected TestDictionary testDictionary;

	protected final List<TestStep> steps = new ArrayList<>();

	protected String pkgAndClassName;

	protected final Multimap<String, TestReference> dynamicReferences = MultimapBuilder.hashKeys().arrayListValues()
			.build();

	protected final Map<String, TestObjectDescription> dynamicDescriptions = new HashMap<>();

	protected TestCase(final String pkgAndClassName, final TestDictionary testDictionary) {
		this.pkgAndClassName = pkgAndClassName;
		this.testDictionary = testDictionary;
	}

	public Metadata getMetadata() {
		return metadata;
	}
	
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	
	public void setDictionary(final TestDictionary testDictionary) {
		this.testDictionary = testDictionary;
	}

	public TestDictionary getDictionary() {
		return testDictionary;
	}

	public void addStep(final TestStep step) {
		steps.add(step);
	}

	public List<TestStep> getSteps() {
		return steps;
	}

	public String getPackage() {
		return pkgAndClassName.substring(0, pkgAndClassName.lastIndexOf('.'));
	}

	public String getName() {
		return pkgAndClassName.substring(pkgAndClassName.lastIndexOf('.') + 1);
	}

	public void setPkgAndClassName(String pkgAndClassName) {
		this.pkgAndClassName = pkgAndClassName;
	}

	public String getPkgAndClassName() {
		return pkgAndClassName;
	}

	public void publishReference(final TestReference reference) {
		dynamicReferences.put(reference.getParameterType(), reference);
		dynamicDescriptions.put(reference.getId(), reference.toDescription());
	}

	public TestObjectDescription descriptionOf(final IdObject idObject) {
		return descriptionOf(idObject.getId());
	}

	public TestObjectDescription descriptionOf(final String id) {
		final var modelDescr = testDictionary.getDescriptions().get(id);
		if (modelDescr != null) {
			return modelDescr;
		}
		final var dynamicDescr = dynamicDescriptions.get(id);
		if (dynamicDescr != null) {
			return dynamicDescr;
		}
		return new TestObjectDescription(id, "");
	}

	public TestReference getReference(final String reference) {
		return dynamicReferences.values().stream().filter(ref -> ref.getName().equals(reference)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unable to find reference [" + reference + ']'));
	}

	public Collection<TestReference> getReferences(final String returnType) {
		final var result = dynamicReferences.get(returnType);
		if (result == null) {
			return Collections.emptyList();
		}
		return result;
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
		return testDictionary.getRoles().values().stream().flatMap(s -> s.getActions().stream())
				.flatMap(a -> a.getParameters().stream()).filter(p -> p.getId().equals(apiParameterId)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Unable to find " + apiParameterId));
	}

}
