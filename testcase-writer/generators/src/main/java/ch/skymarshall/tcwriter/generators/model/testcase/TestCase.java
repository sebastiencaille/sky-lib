package ch.skymarshall.tcwriter.generators.model.testcase;

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

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.test.TestObjectDescription;

public class TestCase {

	@JsonIgnore
	private TestModel testModel;

	private final List<TestStep> steps = new ArrayList<>();
	private final String pathInSrcFolder;
	private final Multimap<String, TestReference> dynamicReferences = MultimapBuilder.hashKeys().arrayListValues()
			.build();

	// description of test variables
	private final Map<String, TestObjectDescription> dynamicDescriptions = new HashMap<>();

	@JsonIgnore
	private Map<String, IdObject> cachedValues = null;

	public TestCase() {
		this.pathInSrcFolder = null;
		this.testModel = null;
	}

	public TestCase(final String path, final TestModel testModel) {
		this.pathInSrcFolder = path;
		this.testModel = testModel;
	}

	public void setModel(final TestModel testModel) {
		this.testModel = testModel;
	}

	public TestModel getModel() {
		return testModel;
	}

	public void addStep(final TestStep step) {
		steps.add(step);
	}

	public List<TestStep> getSteps() {
		return steps;
	}

	public String getFolderinSrc() {
		return pathInSrcFolder.substring(0, pathInSrcFolder.lastIndexOf('.'));
	}

	public String getName() {
		return pathInSrcFolder.substring(pathInSrcFolder.lastIndexOf('.') + 1);
	}

	public String getPathInSrc() {
		return pathInSrcFolder;
	}

	public void publishReference(final TestReference reference) {
		dynamicReferences.put(reference.getType(), reference);
		dynamicDescriptions.put(reference.getId(), reference.toDescription());
	}

	public TestObjectDescription descriptionOf(final IdObject idObject) {
		return descriptionOf(idObject.getId());
	}

	public TestObjectDescription descriptionOf(final String id) {
		final TestObjectDescription modelDescr = testModel.getDescriptions().get(id);
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
				.orElseThrow(() -> new IllegalArgumentException("Unable to find reference " + reference));
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
			cachedValues = testModel.getRoles().values().stream().flatMap(r -> r.getActions().stream())
					.collect(Collectors.toMap(IdObject::getId, a -> a));
			cachedValues.putAll(testModel.getParameterFactories().values().stream()
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
		return steps.stream().flatMap(s -> s.getAction().getParameters().stream())
				.filter(p -> p.getId().equals(apiParameterId)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Unable to find " + apiParameterId));
	}

}
