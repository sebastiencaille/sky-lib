package ch.scaille.tcwriter.gui.frame;

import static ch.scaille.util.helpers.LambdaExt.uncheck;

import java.awt.Dialog.ModalityType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.scaille.gui.mvc.GuiController;
import ch.scaille.gui.swing.tools.SwingGenericEditorDialog;
import ch.scaille.gui.tools.GenericEditorClassModel;
import ch.scaille.gui.tools.GenericEditorController;
import ch.scaille.tcwriter.executors.ITestExecutor;
import ch.scaille.tcwriter.executors.ITestExecutor.TestConfig;
import ch.scaille.tcwriter.gui.DictionaryImport;
import ch.scaille.tcwriter.gui.frame.TCWriterModel.TestExecutionState;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.ExportableTestStep;
import ch.scaille.tcwriter.model.testcase.TestStep;
import ch.scaille.tcwriter.testexec.TestExecutionListener;
import ch.scaille.tcwriter.testexec.TestRemoteControl;
import ch.scaille.util.helpers.Logs;

public class TCWriterController extends GuiController {

    private static final Logger LOGGER = Logs.of(TCWriterController.class);

    private final TCWriterModel model;
    private final TestRemoteControl testRemoteControl;
    private final TCWriterGui gui;
    private final ITestExecutor testExecutor;

    private final IModelDao modelDao;

    public TCWriterController(final IModelDao modelDao, TestDictionary tcDictionary, final ITestExecutor testExecutor)
            throws IOException {
        this.modelDao = modelDao;
        this.testExecutor = testExecutor;
        TestDictionary dictionary = tcDictionary;
        while (dictionary == null) {
            try {
                dictionary = modelDao.readTestDictionary("default").orElseThrow(() -> new FileNotFoundException("default"));
            } catch (FileNotFoundException e) {
                new DictionaryImport(null, modelDao).runImport();
            }
        }

        model = new TCWriterModel(dictionary, getScopedChangeSupport());
        gui = new TCWriterGui(this);
        testRemoteControl = new TestRemoteControl(9998, new TestExecutionListener() {

            @Override
            public void testRunning(boolean running) {
                SwingUtilities.invokeLater(() -> model.getExecutionState().setValue(this,
                        running ? TestExecutionState.RUNNING : TestExecutionState.STOPPED));
            }

            @Override
            public void testPaused(boolean paused) {
                SwingUtilities.invokeLater(() -> model.getExecutionState().setValue(this,
                        paused ? TestExecutionState.PAUSED : TestExecutionState.RUNNING));
            }

        });
    }

    public void run() {
        gui.build();
        activate();
        gui.start();
        newTestCase();
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

    public void editConfig() throws IOException {

        final var dialog = new SwingGenericEditorDialog(gui, "Configuration", ModalityType.DOCUMENT_MODAL);
        final var editor = new GenericEditorController<>(dialog,
                GenericEditorClassModel.builder(modelDao.getConfiguration().getClass()).build());
        editor.activate();
        editor.loadUnsafe(modelDao.getConfiguration());
        dialog.setSize(dialog.getWidth() + 400, dialog.getHeight() + 30);
        dialog.setVisible(true);
        dialog.dispose();
        modelDao.saveConfiguration();
    }

    public void newTestCase() {
        final var newTestCase = new ExportableTestCase("undefined.Undefined", model.getTestDictionary());
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
            addAfter = steps.get(steps.size() - 1);
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
        for (int i = 0; i < testCase.getSteps().size(); i++) {
            final var testStep = testCase.getSteps().get(i);
            if (testStep.getOrdinal() != i + 1) {
                throw new IllegalStateException("Step " + i + ": wrong ordinal " + testStep.getOrdinal());
            }
        }
        model.getTestCase().setValue(this, testCase);
    }

    public void save() throws IOException {
        final var testFileChooser = new JFileChooser();
        testFileChooser.setFileFilter(new FileNameExtensionFilter("JSon test", "json"));
        final int dialogResult = testFileChooser.showSaveDialog(gui);
        if (dialogResult == 0) {
            final var testFile = testFileChooser.getSelectedFile();
            modelDao.writeTestCase(testFile.getName(), model.getTestCase().getValue());
        }
    }

    public void loadTestCase() throws IOException {
        final var testFileChooser = new JFileChooser();
        testFileChooser.setFileFilter(new FileNameExtensionFilter("Json test", "json"));
        final int dialogResult = testFileChooser.showOpenDialog(gui);
        if (dialogResult == 0) {
            final var testFile = testFileChooser.getSelectedFile();
            loadTestCase(modelDao.readTestCase(testFile.getName(), model.getTestDictionary()).orElseThrow(() -> new FileNotFoundException(testFile.getName())));
        }
    }

    public void startTestCase() {
        testRemoteControl.resetConnection();
        new Thread(uncheck(this::runTestCase, gui::handleException), "Test execution").start();
    }

    public void runTestCase() throws IOException, InterruptedException, TestCaseException {
        final int rcPort = testRemoteControl.prepare();
        LOGGER.log(Level.INFO, "Using port {}", rcPort);
        try (var config = new TestConfig(model.getTestCase().getValue(), Files.createTempDirectory("tc"), rcPort)) {
            testExecutor.startTest(config);
            testRemoteControl.controlTest();
        }
    }

    public void generateCode() throws IOException, TestCaseException {
        this.testExecutor.generateCode(this.model.getTestCase().getValue());
    }

    public void importDictionary() {
        boolean imported = new DictionaryImport(gui, modelDao).runImport();
        if (imported) {
            restart();
        }
    }

    public void restart() {
        try {
            gui.setVisible(false);
            new TCWriterController(modelDao, null, testExecutor).run();
        } catch (IOException e) {
            TCWriterGui.handleException(null, e);
        }
    }

    public void resumeTestCase() throws IOException {
        testRemoteControl.resume();
    }

}
