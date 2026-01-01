package ch.scaille.tcwriter.gui.frame;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestStep;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

@Getter
public class TCWriterModel {

	public enum TestExecutionState {
		STOPPED, RUNNING, PAUSED
	}

	private final ObjectProperty<TestCase> testCase;

	private final ObjectProperty<@Nullable TestStep> selectedStep;

	private final ObjectProperty<TestExecutionState> executionState;

   private final ObjectProperty<TestDictionary> testDictionary;

	public TCWriterModel(final IPropertiesGroup changeSupport) {
		this.testCase = new ObjectProperty<>("TestCase", changeSupport, new ExportableTestCase("", new TestDictionary()));
		this.selectedStep = new ObjectProperty<>("SelectedStep", changeSupport, null);
		this.executionState = new ObjectProperty<>("ExecutionState", changeSupport, TestExecutionState.STOPPED);
		this.testDictionary = new ObjectProperty<>("TestDictionary", changeSupport, new TestDictionary());
	}

}
