package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

public class TestCase {

	private final TestModel testModel;

	private final List<TestStep> steps = new ArrayList<>();
	private final String path;
	private final Multimap<String, TestParameter> dynamicReferences = MultimapBuilder.hashKeys().arrayListValues()
			.build();

	private final Map<String, String> dynamicDescriptions = new HashMap<>();

	public TestCase(final String path, final TestModel testModel) {
		this.path = path;
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
		return path.substring(0, path.lastIndexOf("."));
	}

	public String getName() {
		return path.substring(path.lastIndexOf(".") + 1);
	}

	public String getPath() {
		return path;
	}

	public void publishReference(final TestParameter reference) {
		dynamicReferences.put(reference.getType(), reference);
		dynamicDescriptions.put(reference.getId(), "another brand: " + reference.getName());
	}

	public String descriptionOf(final IdObject idObject) {
		final String id = idObject.getId();
		return descriptionOf(id);
	}

	public String descriptionOf(final String id) {
		final String modelDescr = testModel.getDescriptions().get(id);
		if (modelDescr != null) {
			return modelDescr;
		}
		final String dynamicDescr = dynamicDescriptions.get(id);
		if (dynamicDescr != null) {
			return dynamicDescr;
		}
		return id;
	}

	public TestParameter getReference(final String reference) {
		return dynamicReferences.values().stream().filter(ref -> ref.getName().equals(reference)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unable to find reference " + reference));
	}

	public Collection<? extends IdObject> getReferences(final String returnType) {
		return dynamicReferences.get(returnType);
	}

}
