package ch.scaille.tcwriter.gui.frame;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestStep;

public class TCWriterModel {

	public enum TestExecutionState {
		STOPPED, RUNNING, PAUSED
	}

	private final ObjectProperty<TestCase> tc;

	private final ObjectProperty<TestStep> selectedStep;

	private final ObjectProperty<TestExecutionState> executionState;

	private final ObjectProperty<TestDictionary> testDictionary;

	public TCWriterModel(final IPropertiesGroup changeSupport) {
		this.tc = new ObjectProperty<>("TestCase", changeSupport, new ExportableTestCase("", new TestDictionary()));
		this.selectedStep = new ObjectProperty<>("SelectedStep", changeSupport);
		this.executionState = new ObjectProperty<>("ExecutionState", changeSupport, TestExecutionState.STOPPED);
		this.testDictionary = new ObjectProperty<>("TestDictionary", changeSupport, new TestDictionary());
	}

	public ObjectProperty<TestDictionary> getTestDictionary() {
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
