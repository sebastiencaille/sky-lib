package ch.skymarshall.tcwriter.gui;

import static java.util.stream.Collectors.joining;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
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
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.tcwriter.generators.JsonHelper;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.gui.editors.params.TestParameterValueEditorPanel;
import ch.skymarshall.tcwriter.gui.editors.steps.StepEditorModel;
import ch.skymarshall.tcwriter.gui.editors.steps.StepEditorPanel;
import ch.skymarshall.tcwriter.gui.steps.StepsTable;

public abstract class TCWriterGui extends JFrame {

	private static final Logger LOGGER = Logger.getLogger(TCWriterGui.class.getName());

	private final ControllerPropertyChangeSupport changeSupport = new ControllerPropertyChangeSupport(this);

	private final ObjectProperty<TestCase> tc = new ObjectProperty<>("TestCase", changeSupport);

	private final ObjectProperty<TestStep> selectedStep = new ObjectProperty<>("SelectedStep", changeSupport);
	private final TestModel testModel;

	/**
	 *
	 * @param tc
	 * @return the source file
	 * @throws TestCaseException
	 * @throws IOException
	 */
	public abstract File generateCode(TestCase tc) throws TestCaseException, IOException;

	public TCWriterGui(final TestModel testModel) {
		this.testModel = testModel;
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
		generateButton.addActionListener(e -> withException(() -> generateCode(tc.getValue())));

		final JButton runButton = new JButton(icon("media/Play24"));
		runButton.setToolTipText("Start execution");

		final JButton continueButton = new JButton(icon("media/StepForward24"));
		continueButton.setToolTipText("Continue execution");

		final TestRemoteControl testRemoteControl = new TestRemoteControl(9998, r -> runButton.setEnabled(!r),
				continueButton::setEnabled);
		runButton.addActionListener(e -> withException(() -> {
			testRemoteControl.resetConnection();
			new Thread(() -> withException(() -> {
				final int port = testRemoteControl.prepare();
				LOGGER.log(Level.INFO, "Using port " + port);
				final TestCase testCase = tc.getValue();
				final File testCaseSouceFolder = generateCode(testCase);
				startTestCase(testCaseSouceFolder, testCase.getFolderInSrc() + "." + testCase.getName(), port);
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

	public void loadTestCase(final Path testFile) throws IOException {
		final TestCase testCase = JsonHelper
				.testCaseFromJson(new String(Files.readAllBytes(testFile), StandardCharsets.UTF_8), testModel);
		loadTestCase(testCase);
	}

	private void save() throws IOException {
		final JFileChooser testFileChooser = new JFileChooser();
		testFileChooser.setFileFilter(new FileNameExtensionFilter("JSon test", "json"));
		final int dialogResult = testFileChooser.showSaveDialog(this);
		if (dialogResult == 0) {
			final File testFile = testFileChooser.getSelectedFile();
			Files.write(testFile.toPath(), JsonHelper.toJson(tc.getValue()).getBytes(StandardCharsets.UTF_8),
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}
	}

	private void loadTestCase() throws IOException {
		final JFileChooser testFileChooser = new JFileChooser();
		testFileChooser.setFileFilter(new FileNameExtensionFilter("JSon test", "json"));
		final int dialogResult = testFileChooser.showOpenDialog(this);
		if (dialogResult == 0) {
			final File testFile = testFileChooser.getSelectedFile();
			loadTestCase(testFile.toPath());
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

	private static class StreamHandler implements Runnable {

		private final InputStream in;
		private final Consumer<String> flow;

		public StreamHandler(final InputStream in, final Consumer<String> flow) {
			this.in = in;
			this.flow = flow;
		}

		public void start() {
			new Thread(this).start();
		}

		@Override
		public void run() {
			try {
				final byte[] buffer = new byte[1024 * 1024];
				int read;
				while ((read = in.read(buffer, 0, buffer.length)) >= 0) {
					flow.accept(new String(buffer, 0, read));
				}
			} catch (final IOException e) {
				// ignore
			}
		}
	}

	public void startTestCase(final File sourceFile, final String className, final int tcpPort)
			throws IOException, InterruptedException {

		final String currentClassPath = Arrays
				.stream(((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs()).map(URL::getFile)
				.collect(joining(":"));
		final String waveClassPath = Arrays
				.stream(((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs())
				.filter(j -> j.toString().contains("testcase-writer") && j.toString().contains("annotations"))
				.map(URL::getFile).collect(joining(":"));
		final Path tmp = new File(System.getProperty("java.io.tmpdir")).toPath();
		final Process testCompiler = new ProcessBuilder("java", //
				"-cp", currentClassPath, //
				"org.aspectj.tools.ajc.Main", //
				"-aspectpath", waveClassPath, //
				"-source", "1.8", //
				"-target", "1.8", //
				// "-verbose", "-showWeaveInfo", //
				"-d", tmp.resolve("tc").toString(), //
				sourceFile.toString() //
		).redirectErrorStream(true).start();
		new StreamHandler(testCompiler.getInputStream(), LOGGER::info).start();
		if (testCompiler.waitFor() != 0) {
			throw new IllegalStateException("Compiler failed with status " + testCompiler.exitValue());
		}

		final Process runTest = new ProcessBuilder("java", "-cp", tmp.resolve("tc") + ":" + currentClassPath,
				"-Dtest.port=" + tcpPort, "-Dtc.stepping=true", "org.junit.runner.JUnitCore", className)
						.redirectErrorStream(true).start();
		new StreamHandler(runTest.getInputStream(), LOGGER::info).start();
	}

}
