package ch.scaille.tcwriter.gui.frame;

import ch.scaille.gui.mvc.IScopedSupport;
import ch.scaille.gui.mvc.properties.ObjectProperty;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestStep;

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

	public ObjectProperty<TestCase> getTestCase() {
		return tc;
	}

	public ObjectProperty<TestStep> getSelectedStep() {
		return selectedStep;
	}

	public ObjectProperty<TestExecutionState> getExecutionState() {
		return executionState;
	}
}
