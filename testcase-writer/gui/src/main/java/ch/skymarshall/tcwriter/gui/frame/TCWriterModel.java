package ch.skymarshall.tcwriter.gui.frame;

import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.tcwriter.generators.model.testapi.TestDictionary;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;

public class TCWriterModel {

	public enum TestExecutionState {
		STOPPED, RUNNING, PAUSED
	}

	private final ObjectProperty<TestCase> tc;

	private final ObjectProperty<TestStep> selectedStep;

	private final ObjectProperty<TestExecutionState> executionState;

	private final TestDictionary testDictionary;

	public TCWriterModel(final TestDictionary testDictionary, final IScopedSupport changeSupport) {
		this.testDictionary = testDictionary;
		tc = new ObjectProperty<>("TestCase", changeSupport);
		selectedStep = new ObjectProperty<>("SelectedStep", changeSupport);
		executionState = new ObjectProperty<>("ExecutionState", changeSupport, TestExecutionState.STOPPED);
	}

	public TestDictionary getTestDictionary() {
		return testDictionary;
	}

	public ObjectProperty<TestCase> getTc() {
		return tc;
	}

	public ObjectProperty<TestStep> getSelectedStep() {
		return selectedStep;
	}

	public ObjectProperty<TestExecutionState> getExecutionState() {
		return executionState;
	}
}
