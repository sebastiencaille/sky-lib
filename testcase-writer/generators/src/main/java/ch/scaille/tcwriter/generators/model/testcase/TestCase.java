package ch.scaille.tcwriter.generators.model.testcase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import ch.scaille.tcwriter.generators.model.IdObject;
import ch.scaille.tcwriter.generators.model.testapi.TestApiParameter;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.tc.TestObjectDescription;

public class TestCase {

	@JsonIgnore
	private TestDictionary testDictionary;

	private final List<TestStep> steps = new ArrayList<>();
	private final Multimap<String, TestReference> dynamicReferences = MultimapBuilder.hashKeys().arrayListValues()
			.build();

	// description of test variables
	private final Map<String, TestObjectDescription> dynamicDescriptions = new HashMap<>();

	private String pkgAndClassName;

	@JsonIgnore
	private Map<String, IdObject> cachedValues = null;

	/**
	 * For json
	 * 
	 * @Deprecated: for JSON
	 */
	@Deprecated
	public TestCase() {
		this.pkgAndClassName = null;
		this.testDictionary = null;
	}

	public TestCase(final String path, final TestDictionary testDictionary) {
		this.pkgAndClassName = path;
		this.testDictionary = testDictionary;
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

	public String getPackageAndClassName() {
		return pkgAndClassName;
	}

	public void publishReference(final TestReference reference) {
		dynamicReferences.put(reference.getType(), reference);
		dynamicDescriptions.put(reference.getId(), reference.toDescription());
	}

	public TestObjectDescription descriptionOf(final IdObject idObject) {
		return descriptionOf(idObject.getId());
	}

	public TestObjectDescription descriptionOf(final String id) {
		final TestObjectDescription modelDescr = testDictionary.getDescriptions().get(id);
		if (modelDescr != null) {
			return modelDescr;
		}
		final TestObjectDescription dynamicDescr = dynamicDescriptions.get(id);
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
		final Collection<TestReference> result = dynamicReferences.get(returnType);
		if (result == null) {
			return Collections.emptyList();
		}
		return result;
	}

	public Collection<TestReference> getSuitableReferences(final TestApiParameter param) {
		return getReferences(param.getType());
	}

	public synchronized IdObject getRestoreValue(final String id) {
		if (cachedValues == null) {
			cachedValues = testDictionary.getRoles().values().stream().flatMap(r -> r.getActions().stream())
					.collect(Collectors.toMap(IdObject::getId, a -> a));
			cachedValues.putAll(testDictionary.getParameterFactories().values().stream()
					.collect(Collectors.toMap(IdObject::getId, a -> a)));
		}

		IdObject restoredObject = cachedValues.get(id);
		if (restoredObject == null) {
			restoredObject = getReference(id);
		}
		if (restoredObject == null) {
			throw new IllegalArgumentException("No cached value for " + id);
		}
		return restoredObject;
	}

	public void fixOrdinals() {
		for (int i = 0; i < steps.size(); i++) {
			steps.get(i).setOrdinal(i + 1);
		}
	}

	public TestApiParameter getTestApi(final String apiParameterId) {
		if (apiParameterId.isEmpty()) {
			return TestApiParameter.NO_PARAMETER;
		}
		return testDictionary.getRoles().values().stream().flatMap(s -> s.getActions().stream())
				.flatMap(a -> a.getParameters().stream()).filter(p -> p.getId().equals(apiParameterId)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Unable to find " + apiParameterId));
	}

}
