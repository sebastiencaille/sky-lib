package ch.skymarshall.tcwriter.gui;

import static ch.skymarshall.tcwriter.generators.JsonHelper.readFile;
import static ch.skymarshall.tcwriter.generators.JsonHelper.testCaseFromJson;
import static ch.skymarshall.tcwriter.generators.JsonHelper.toJson;
import static ch.skymarshall.tcwriter.generators.JsonHelper.writeFile;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.gui.editors.params.TestParameterValueEditorPanel;
import ch.skymarshall.tcwriter.gui.editors.steps.StepEditorModel;
import ch.skymarshall.tcwriter.gui.editors.steps.StepEditorPanel;
import ch.skymarshall.tcwriter.gui.steps.StepsTable;
import executors.ITestExecutor;

public class TCWriterGui extends JFrame {

	private static final Logger LOGGER = Logger.getLogger(TCWriterGui.class.getName());

	private final IScopedSupport changeSupport = new ControllerPropertyChangeSupport(this).byContainer(this);

	private final TestModel testModel;

	private final ObjectProperty<TestCase> tc = new ObjectProperty<>("TestCase", changeSupport);

	private final ObjectProperty<TestStep> selectedStep = new ObjectProperty<>("SelectedStep", changeSupport);

	private final ITestExecutor testExecutor;

	public TCWriterGui(final TestModel testModel, final ITestExecutor testExecutor) {
		this.testModel = testModel;
		this.testExecutor = testExecutor;
	}

	public void run() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.getContentPane().setLayout(new BorderLayout());

		final JButton newTCButton = new JButton(icon("general/New24"));
		newTCButton.setToolTipText("New test case");
		newTCButton.addActionListener(e -> withException(this::newTestCase));

		final JButton loadButton = new JButton(icon("general/Open24"));
		loadButton.setToolTipText("Open test case");
		loadButton.addActionListener(e -> withException(this::loadTestCase));

		final JButton saveButton = new JButton(icon("general/Save24"));
		saveButton.setToolTipText("Save test case");
		saveButton.addActionListener(e -> withException(this::save));

		final JButton generateButton = new JButton(icon("general/Export24"));
		generateButton.setToolTipText("Export to Java");
		generateButton.addActionListener(e -> withException(() -> testExecutor.generateCode(tc.getValue())));

		final JButton runButton = new JButton(icon("media/Play24"));
		runButton.setToolTipText("Start execution");

		final JButton continueButton = new JButton(icon("media/StepForward24"));
		continueButton.setToolTipText("Continue execution");

		final TestRemoteControl testRemoteControl = new TestRemoteControl(9998, r -> runButton.setEnabled(!r),
				continueButton::setEnabled);
		runButton.addActionListener(e -> withException(() -> {
			testRemoteControl.resetConnection();
			new Thread(() -> withException(() -> {
				final int rcPort = testRemoteControl.prepare();
				LOGGER.log(Level.INFO, "Using port " + rcPort);
				testExecutor.runTest(tc.getValue(), rcPort);
				testRemoteControl.start();
			}), "Test execution").start();
		}));
		continueButton.addActionListener(e -> withException(testRemoteControl::resume));
		continueButton.setEnabled(false);

		final JSeparator sep = new JSeparator(SwingConstants.VERTICAL);

		final JButton addStepButton = new JButton(icon("table/RowInsertAfter24"));
		addStepButton.setToolTipText("Add step");
		addStepButton.addActionListener(e -> withException(this::addStep));

		final JButton removeStepButton = new JButton(icon("table/RowDelete24"));
		removeStepButton.setToolTipText("Remove step");
		removeStepButton.addActionListener(e -> withException(this::removeStep));

		final StepsTable stepsTable = new StepsTable(tc, selectedStep, testRemoteControl);

		final JToolBar buttons = new JToolBar();
		buttons.add(newTCButton);
		buttons.add(loadButton);
		buttons.add(saveButton);
		buttons.add(generateButton);
		buttons.add(runButton);
		buttons.add(continueButton);
		buttons.add(sep);
		buttons.add(addStepButton);
		buttons.add(removeStepButton);
		this.getContentPane().add(buttons, BorderLayout.NORTH);

		final StepEditorModel stepEditorModel = new StepEditorModel(changeSupport);
		final StepEditorPanel stepEditor = new StepEditorPanel(stepEditorModel, testModel, selectedStep);

		final JComponent selectorEditor = new TestParameterValueEditorPanel("selector", changeSupport, tc,
				stepEditorModel.getSelectorValue(), stepEditorModel.getSelector());
		final JComponent param0Editor = new TestParameterValueEditorPanel("param0", changeSupport, tc,
				stepEditorModel.getActionParameterValue(), stepEditorModel.getActionParameter());

		final JSplitPane paramsPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(selectorEditor),
				new JScrollPane(param0Editor));

		stepEditorModel.getSelectorValue().bind(v -> !v.equals(TestParameterValue.NO_VALUE)) //
				.listen(selectorEditor::setVisible);

		stepEditorModel.getActionParameterValue().bind(v -> !v.equals(TestParameterValue.NO_VALUE))
				.listen(param0Editor::setVisible);

		final JScrollPane stepsPane = new JScrollPane(stepsTable);
		final JScrollPane stepPane = new JScrollPane(stepEditor);
		final JScrollPane paramPane = new JScrollPane(paramsPane);

		final int height = 1200;

		final JSplitPane topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, stepsPane, stepPane);
		final JSplitPane bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, paramPane);
		topSplit.setDividerLocation(height / 3);
		bottomSplit.setDividerLocation(height / 2);

		this.getContentPane().add(bottomSplit, BorderLayout.CENTER);

		changeSupport.attachAll();

		this.validate();
		this.pack();
		this.setSize(1600, height);
		this.setVisible(true);
	}

	private ImageIcon icon(final String name) {
		final String resourceName = "toolbarButtonGraphics/" + name + ".gif";
		final URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
		if (resource == null) {
			throw new IllegalStateException("Unable to find resource " + resourceName);
		}
		return new ImageIcon(resource);
	}

	public void newTestCase() {
		final TestCase newTestCase = new TestCase();
		newTestCase.setModel(testModel);
		newTestCase.addStep(new TestStep(1));
		tc.setValue(this, newTestCase);
	}

	public void addStep() {
		final TestCase testCase = tc.getValue();

		TestStep addAfter;
		if (selectedStep.getValue() != null) {
			addAfter = selectedStep.getValue();
		} else {
			final List<TestStep> steps = testCase.getSteps();
			addAfter = steps.get(steps.size() - 1);
		}

		final TestStep newStep = addAfter.duplicate();
		testCase.getSteps().add(addAfter.getOrdinal(), newStep);
		testCase.fixOrdinals();
		tc.forceChanged(this);
	}

	public void removeStep() {
		final TestCase testCase = tc.getValue();
		if (testCase.getSteps().size() == 1) {
			return;
		}
		if (selectedStep.getValue() == null) {
			return;
		}
		testCase.getSteps().remove(selectedStep.getValue().getOrdinal() - 1);
		testCase.fixOrdinals();
		tc.forceChanged(this);
	}

	public void loadTestCase(final TestCase testCase) {
		for (int i = 0; i < testCase.getSteps().size(); i++) {
			final TestStep testStep = testCase.getSteps().get(i);
			if (testStep.getOrdinal() != i + 1) {
				throw new IllegalStateException("Step " + i + ": wrong ordinal " + testStep.getOrdinal());
			}
		}
		tc.setValue(this, testCase);
	}

	private void save() throws IOException {
		final JFileChooser testFileChooser = new JFileChooser();
		testFileChooser.setFileFilter(new FileNameExtensionFilter("JSon test", "json"));
		final int dialogResult = testFileChooser.showSaveDialog(this);
		if (dialogResult == 0) {
			final File testFile = testFileChooser.getSelectedFile();
			writeFile(testFile.toPath(), toJson(tc.getValue()));
		}
	}

	private void loadTestCase() throws IOException {
		final JFileChooser testFileChooser = new JFileChooser();
		testFileChooser.setFileFilter(new FileNameExtensionFilter("JSon test", "json"));
		final int dialogResult = testFileChooser.showOpenDialog(this);
		if (dialogResult == 0) {
			final File testFile = testFileChooser.getSelectedFile();
			loadTestCase(testCaseFromJson(readFile(testFile.toPath()), testModel));
		}
	}

	private interface EventWithException<E extends Exception> {

		void execute() throws E;

	}

	private <E extends Exception> void withException(final EventWithException<E> e) {
		try {
			e.execute();
		} catch (final Exception ex) {
			LOGGER.log(Level.WARNING, "Unable to start testcase", ex);
			JOptionPane.showMessageDialog(this, "Unable to execution action: " + ex.getMessage());
		}

	}

}
