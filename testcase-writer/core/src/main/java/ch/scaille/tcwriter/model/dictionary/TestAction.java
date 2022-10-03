package ch.scaille.tcwriter.model.dictionary;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.NamedObject;

public class TestAction extends NamedObject {

	public static final TestAction NOT_SET = new TestAction(IdObject.ID_NOT_SET, "", "", new StepClassifier[0]);

	private final List<TestApiParameter> parameters = new ArrayList<>();

	private final StepClassifier[] allowedClassifiers;

	private final String returnType;

	protected TestAction() {
		super(null, null);
		this.returnType = null;
		this.allowedClassifiers = new StepClassifier[0];
	}

	public TestAction(final String id, final String name, final String returnType,
			StepClassifier[] allowedClassifiers) {
		super(id, name);
		this.returnType = returnType;
		this.allowedClassifiers = allowedClassifiers;
	}

	public StepClassifier[] getAllowedClassifiers() {
		return allowedClassifiers;
	}

	public List<TestApiParameter> getParameters() {
		return parameters;
	}

	public TestApiParameter getParameter(final int index) {
		return parameters.get(index);
	}

	public String getReturnType() {
		return returnType;
	}

	@Override
	public String toString() {
		return getName() + ": " + parameters.stream().map(TestApiParameter::getParameterType).collect(joining(","));
	}

}
