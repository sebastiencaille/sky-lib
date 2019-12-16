package ch.skymarshall.tcwriter.gui.frame;

import java.awt.Dialog.ModalityType;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.swing.tools.SwingGenericEditorDialog;
import ch.skymarshall.gui.tools.ClassAdapter;
import ch.skymarshall.gui.tools.GenericEditorAdapter;
import ch.skymarshall.tcwriter.generators.GeneratorConfig;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.persistence.IModelPersister;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.gui.TestRemoteControl;
import ch.skymarshall.tcwriter.gui.frame.TCWriterModel.TestExecutionState;
import executors.ITestExecutor;

public class TCWriterController {

	private static final Logger LOGGER = Logger.getLogger(TCWriterController.class.getName());

	private final TCWriterModel model;
	private final TestRemoteControl testRemoteControl;
	private final TCWriterGui gui;
	private final IScopedSupport changeSupport;
	private final ITestExecutor testExecutor;

	private final IModelPersister persister;

	private final GeneratorConfig config;

	public TCWriterController(final GeneratorConfig config, final IModelPersister persister,
			final ITestExecutor testExecutor) throws IOException {
		this.config = config;
		this.persister = persister;
		this.testExecutor = testExecutor;
		changeSupport = new ControllerPropertyChangeSupport(this).scoped(this);
		model = new TCWriterModel(persister.readTestModel(), changeSupport);
		gui = new TCWriterGui(this);
		testRemoteControl = new TestRemoteControl(9998,
				r -> SwingUtilities.invokeLater(() -> model.getExecutionState().setValue(this,
						r ? TestExecutionState.RUNNING : TestExecutionState.STOPPED)),
				p -> SwingUtilities.invokeLater(() -> model.getExecutionState().setValue(this,
						p ? TestExecutionState.PAUSED : TestExecutionState.RUNNING)));
	}

	public void run() {
		gui.run();
		changeSupport.attachAll();
	}

	public IScopedSupport getChangeSupport() {
		return changeSupport;
	}

	public TCWriterModel getModel() {
		return model;
	}

	public TestRemoteControl getTestRemoteControl() {
		return testRemoteControl;
	}

	public void editConfig() throws IOException {

		final SwingGenericEditorDialog dialog = new SwingGenericEditorDialog(gui, "Configuration",
				ModalityType.DOCUMENT_MODAL);
		final GenericEditorAdapter<GeneratorConfig, ?> editor = new GenericEditorAdapter<>(dialog,
				new ClassAdapter<>(GeneratorConfig.class));
		editor.apply();
		editor.load(config);
		dialog.setSize(dialog.getWidth() + 400, dialog.getHeight() + 30);
		dialog.setVisible(true);
		persister.writeConfiguration(config);
	}

	public void newTestCase() {
		final TestCase newTestCase = new TestCase();
		newTestCase.setModel(model.getTestModel());
		newTestCase.addStep(new TestStep(1));
		model.getTc().setValue(this, newTestCase);
	}

	public void addStep() {
		final TestCase testCase = model.getTc().getValue();

		TestStep addAfter;
		if (model.getSelectedStep().getValue() != null) {
			addAfter = model.getSelectedStep().getValue();
		} else {
			final List<TestStep> steps = testCase.getSteps();
			addAfter = steps.get(steps.size() - 1);
		}

		final TestStep newStep = addAfter.duplicate();
		testCase.getSteps().add(addAfter.getOrdinal(), newStep);
		testCase.fixOrdinals();
		model.getTc().forceChanged(this);
	}

	public void removeStep() {
		final TestCase testCase = model.getTc().getValue();
		if (testCase.getSteps().size() == 1) {
			return;
		}
		if (model.getSelectedStep().getValue() == null) {
			return;
		}
		testCase.getSteps().remove(model.getSelectedStep().getValue().getOrdinal() - 1);
		testCase.fixOrdinals();
		model.getTc().forceChanged(this);
	}

	public void loadTestCase(final TestCase testCase) {
		for (int i = 0; i < testCase.getSteps().size(); i++) {
			final TestStep testStep = testCase.getSteps().get(i);
			if (testStep.getOrdinal() != i + 1) {
				throw new IllegalStateException("Step " + i + ": wrong ordinal " + testStep.getOrdinal());
			}
		}
		model.getTc().setValue(this, testCase);
	}

	public void save() throws IOException {
		final JFileChooser testFileChooser = new JFileChooser();
		testFileChooser.setFileFilter(new FileNameExtensionFilter("JSon test", "json"));
		final int dialogResult = testFileChooser.showSaveDialog(gui);
		if (dialogResult == 0) {
			final File testFile = testFileChooser.getSelectedFile();
			persister.writeTestCase(testFile.toString(), model.getTc().getValue());
		}
	}

	public void loadTestCase() throws IOException {
		final JFileChooser testFileChooser = new JFileChooser();
		testFileChooser.setFileFilter(new FileNameExtensionFilter("JSon test", "json"));
		final int dialogResult = testFileChooser.showOpenDialog(gui);
		if (dialogResult == 0) {
			final File testFile = testFileChooser.getSelectedFile();
			loadTestCase(persister.readTestCase(testFile.toString(), model.getTestModel()));
		}
	}

	public void startTestCase() {
		testRemoteControl.resetConnection();
		new Thread(() -> gui.withException(this::runTestCase), "Test execution").start();
	}

	public void runTestCase() throws IOException, InterruptedException, TestCaseException {
		final int rcPort = testRemoteControl.prepare();
		LOGGER.log(Level.INFO, "Using port " + rcPort);
		testExecutor.runTest(model.getTc().getValue(), rcPort);
		testRemoteControl.start();
	}

	public void generateCode() throws IOException, TestCaseException {
		testExecutor.generateCode(model.getTc().getValue());
	}

	public void resumeTestCase() throws IOException {
		testRemoteControl.resume();
	}

}
