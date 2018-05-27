package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCase {

	private final TestModel testModel;
	private final Map<String, TestValue> references = new HashMap<>();

	private final List<TestStep> steps = new ArrayList<>();
	private final String path;

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

}
