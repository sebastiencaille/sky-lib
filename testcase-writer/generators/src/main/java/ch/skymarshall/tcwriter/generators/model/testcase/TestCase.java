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
import ch.skymarshall.tcwriter.generators.model.ObjectDescription;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;

public class TestCase {

	@JsonIgnore
	private TestModel testModel;

	private final List<TestStep> steps = new ArrayList<>();
	private final String path;
	private final Multimap<String, TestReference> dynamicReferences = MultimapBuilder.hashKeys().arrayListValues()
			.build();

	// description of test variables
	private final Map<String, ObjectDescription> dynamicDescriptions = new HashMap<>();

	@JsonIgnore
	private Map<String, IdObject> cachedValues = null;

	public TestCase() {
		this.path = null;
		this.testModel = null;
	}

	public TestCase(final String path, final TestModel testModel) {
		this.path = path;
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

	public String getFolder() {
		return path.substring(0, path.lastIndexOf('.'));
	}

	public String getName() {
		return path.substring(path.lastIndexOf('.') + 1);
	}

	public String getPath() {
		return path;
	}

	public void publishReference(final TestReference reference) {
		dynamicReferences.put(reference.getType(), reference);
		dynamicDescriptions.put(reference.getId(), reference.toDescription());
	}

	public ObjectDescription descriptionOf(final IdObject idObject) {
		return descriptionOf(idObject.getId());
	}

	public ObjectDescription descriptionOf(final String id) {
		final ObjectDescription modelDescr = testModel.getDescriptions().get(id);
		if (modelDescr != null) {
			return modelDescr;
		}
		final ObjectDescription dynamicDescr = dynamicDescriptions.get(id);
		if (dynamicDescr != null) {
			return dynamicDescr;
		}
		return new ObjectDescription(id, "");
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

	public Collection<TestReference> getReferences(final TestApiParameter param) {
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

	public TestApiParameter getTypeOf(final String apiParameterId) {
		return steps.stream().flatMap(s -> s.getAction().getParameters().stream())
				.filter(p -> p.getId().equals(apiParameterId)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Unable to find " + apiParameterId));
	}

}
