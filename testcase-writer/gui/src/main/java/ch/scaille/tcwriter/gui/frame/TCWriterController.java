package ch.scaille.tcwriter.gui.frame;

import static ch.scaille.util.helpers.LambdaExt.uncheckedF2;
import static ch.scaille.util.helpers.LambdaExt.uncheckedR;

import java.awt.Dialog.ModalityType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.scaille.gui.mvc.GuiController;
import ch.scaille.gui.swing.tools.SwingGenericEditorDialog;
import ch.scaille.gui.tools.GenericEditorClassModel;
import ch.scaille.gui.tools.GenericEditorController;
import ch.scaille.gui.tools.IGenericEditor;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.properties.ErrorSet;
import ch.scaille.tcwriter.gui.editors.steps.StepEditorController;
import ch.scaille.tcwriter.gui.frame.TCWriterModel.TestExecutionState;
import ch.scaille.tcwriter.gui.utils.DictionaryImport;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.ExportableTestStep;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestStep;
import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.ModelDao;
import ch.scaille.tcwriter.services.testexec.ITestExecutor;
import ch.scaille.tcwriter.services.testexec.ITestExecutor.TestConfig;
import ch.scaille.tcwriter.services.testexec.TestExecutionListener;
import ch.scaille.tcwriter.services.testexec.TestRemoteControl;
import ch.scaille.util.helpers.Logs;

public class TCWriterController extends GuiController {

	private static final Logger LOGGER = Logs.of(TCWriterController.class);

	private final TCWriterModel model;

	private final TestRemoteControl testRemoteControl;
	private final TCWriterGui gui;
	private final ITestExecutor testExecutor;
	private final IConfigDao configDao;
	private final ModelDao modelDao;

	private final StepEditorController stepEditorController;
	
	public TCWriterController(final IConfigDao configDao, final ModelDao modelDao, TestDictionary tcDictionary,
			final ITestExecutor testExecutor) {
		this.configDao = configDao;
		this.modelDao = modelDao;
		this.testExecutor = testExecutor;
		
		this.model = new TCWriterModel(getScopedChangeSupport());

		this.stepEditorController = new StepEditorController(this);

		this.testRemoteControl = new TestRemoteControl(9998, new TestExecutionListener() {

			@Override
			public void testRunning(boolean running) {
				SwingUtilities.invokeLater(() -> model.getExecutionState()
						.setValue(this, running ? TestExecutionState.RUNNING : TestExecutionState.STOPPED));
			}

			@Override
			public void testPaused(boolean paused) {
				SwingUtilities.invokeLater(() -> model.getExecutionState()
						.setValue(this, paused ? TestExecutionState.PAUSED : TestExecutionState.RUNNING));
			}

		});

		var dictionary = tcDictionary;
		if (dictionary == null) {
			dictionary = loadDictionary(null);
		}
		model.getTestDictionary().setValue(this, dictionary);
		
		this.gui = new TCWriterGui(this);
	}

	public StepEditorController getStepEditorController() {
		return stepEditorController;
	}
	
	public TestDictionary loadDictionary(String name) {
		TestDictionary dictionary = null;
		while (dictionary == null) {
			try {
				dictionary = modelDao.readTestDictionary(name)
						.orElseThrow(() -> new FileNotFoundException("default"));
			} catch (FileNotFoundException e) {
				// UI that allows selecting a dictionary
				new DictionaryImport(null, modelDao).runImport();
			}
		}
		return dictionary;
	}

	public TCWriterModel getModel() {
		return model;
	}

	public TestRemoteControl getTestRemoteControl() {
		return testRemoteControl;
	}

	public TCWriterGui getGui() {
		return gui;
	}

	public void editConfig() {
		final var configEditorDialog = new SwingGenericEditorDialog<>(gui, "Configuration", ModalityType.DOCUMENT_MODAL);
		final var editorPropertySupport = PropertyChangeSupportController.mainGroup(configEditorDialog);
		final var errorProp = new ErrorSet("Error", editorPropertySupport);
		for (final var configToEdit : configDao.getCurrentConfig().getSubconfigs()) {
			final var builder = GenericEditorClassModel.builder((Class<Object>)configToEdit.getClass())
					.with(propertySupport)
					.with(errorProp);
			configEditorDialog.add(createEditor(configToEdit,
					configEditorDialog.tab(configToEdit.getClass().getSimpleName()), builder));
		}
		configEditorDialog.build(errorProp);
		configEditorDialog.setSize(configEditorDialog.getWidth() + 400, configEditorDialog.getHeight() + 30);
		configEditorDialog.setVisible(true);
		configEditorDialog.dispose();
		configDao.saveConfiguration();
	}

	private static GenericEditorController<Object> createEditor(Object configToEdit, IGenericEditor<Object> tab,
			GenericEditorClassModel.Builder<Object> builder) {
		final var configEditor = new GenericEditorController<>(tab, builder.build());
		configEditor.activate();
		configEditor.loadUnsafe(configToEdit);
		return configEditor;
	}

	public void newTestCase() {
		final var newTestCase = new ExportableTestCase("undefined.Undefined", model.getTestDictionary().getValue());
		newTestCase.addStep(new ExportableTestStep(1));
		model.getTestCase().setValue(this, newTestCase);
	}

	/**
	 * If no step selected, add to last step. If step selected, add after selected
	 * step
	 */
	public void addStep() {
		final var testCase = model.getTestCase().getValue();

		TestStep addAfter;
		if (model.getSelectedStep().getValue() != null) {
			addAfter = model.getSelectedStep().getValue();
		} else {
			final List<TestStep> steps = testCase.getSteps();
			addAfter = steps.getLast();
		}

		final var newStep = addAfter.duplicate();
		testCase.getSteps().add(addAfter.getOrdinal(), newStep);
		testCase.fixOrdinals();
		model.getTestCase().forceChanged(this);
		model.getSelectedStep().setValue(this, newStep);
	}

	public void removeStep() {
		final var testCase = model.getTestCase().getValue();
		if (testCase.getSteps().size() == 1) {
			return;
		}
		if (model.getSelectedStep().getValue() == null) {
			return;
		}
		testCase.getSteps().remove(model.getSelectedStep().getValue().getOrdinal() - 1);
		testCase.fixOrdinals();
		model.getTestCase().forceChanged(this);
	}

	public void loadTestCase(final TestCase testCase) {
		model.getSelectedStep().setValue(this, null);
		for (int i = 0; i < testCase.getSteps().size(); i++) {
			final var testStep = testCase.getSteps().get(i);
			if (testStep.getOrdinal() != i + 1) {
				throw new IllegalStateException("Step " + i + ": wrong ordinal " + testStep.getOrdinal());
			}
		}
		model.getTestCase().setValue(this, testCase);
	}

	public void save() {
		final var testFileChooser = fileChooser();
		final var dialogResult = testFileChooser.showSaveDialog(gui);
		if (dialogResult == 0) {
			final var testFile = testFileChooser.getSelectedFile();
			modelDao.writeTestCase(testFile.toString(), (ExportableTestCase) model.getTestCase().getValue());
		}
	}

	private JFileChooser fileChooser() {
		final var testFileChooser = new JFileChooser();
		testFileChooser.setFileFilter(new FileNameExtensionFilter("Test case", "yaml"));
		testFileChooser.setCurrentDirectory(new File(modelDao.getCurrentConfig().getTcPath()));
		return testFileChooser;
	}

	public void loadTestCase() throws IOException {
		final var testFileChooser = fileChooser();
		final int dialogResult = testFileChooser.showOpenDialog(gui);
		if (dialogResult == 0) {
			final var testFile = testFileChooser.getSelectedFile();
			loadTestCase(modelDao.readTestCase(testFile.toString(), this::loadDictionary)
					.orElseThrow(() -> new FileNotFoundException(testFile.getName())));
		}
	}

	public void startTestCase() {
		testRemoteControl.resetConnection();
		new Thread(uncheckedR(this::runTestCase, gui::handleException), "Test execution").start();
	}

	public void runTestCase() throws IOException, InterruptedException, TestCaseException {
		final int rcPort = testRemoteControl.prepare();
		LOGGER.info(() -> "Using port " + rcPort);
		try (var config = new TestConfig(model.getTestCase().getValue(), Files.createTempDirectory("tc"), rcPort)) {
			testExecutor.startTest(config);
			testRemoteControl.controlTest(model.getTestCase().getValue().getSteps().size());
		}
	}

	public void generateCode() throws TestCaseException {
		this.testExecutor.createTemplate(this.model.getTestCase().getValue())
				.writeTo(uncheckedF2(this.modelDao::writeTestCaseCode))
				.getStorageLocator();
	}

	public void importDictionary() {
		final var imported = new DictionaryImport(gui, modelDao).runImport();
		if (imported) {
			restart();
		}
	}

	public void start() {
		stepEditorController.activate();
		activate();
		gui.start();
		newTestCase();
	}
	
	public void restart() {
		gui.setVisible(false);
		new TCWriterController(configDao, modelDao, null, testExecutor).start();
	}

	public void resumeTestCase() throws IOException {
		testRemoteControl.resume();
	}

}
