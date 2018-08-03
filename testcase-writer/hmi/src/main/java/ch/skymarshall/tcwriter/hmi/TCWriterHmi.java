package ch.skymarshall.tcwriter.hmi;

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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;

import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.generators.visitors.JsonHelper;
import ch.skymarshall.tcwriter.hmi.steps.StepsTable;

public abstract class TCWriterHmi extends JFrame {

	private static final Logger LOGGER = Logger.getLogger(TCWriterHmi.class.getName());

	private final ControllerPropertyChangeSupport changeSupport = new ControllerPropertyChangeSupport(this);

	private final ObjectProperty<TestCase> testCaseProperty = new ObjectProperty<>("TestCase", changeSupport);

	private final TestModel testModel;

	private final ObjectProperty<TestStep> selectedStep = new ObjectProperty<>("SelectedStep", changeSupport);

	public abstract File generateCode(TestCase tc) throws TestCaseException, IOException;

	public TCWriterHmi(final TestModel testModel) {
		this.testModel = testModel;
	}

	public TCWriterHmi(final Path modelPath) throws IOException {
		testModel = JsonHelper.testModelFromJson(Files.readAllLines(modelPath).get(0));
	}

	public void run() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final TestRemoteControl testRemoteControl = new TestRemoteControl(9998);

		this.getContentPane().setLayout(new BorderLayout());

		final JButton newTCButton = new JButton(icon("general/New24"));
		newTCButton.addActionListener(e -> withException(this::newTestCase));

		final JButton loadButton = new JButton(icon("general/Open24"));
		loadButton.addActionListener(e -> withException(this::loadTestCase));

		final JButton saveButton = new JButton(icon("general/Save24"));
		saveButton.addActionListener(e -> withException(this::save));

		final JButton generateButton = new JButton(icon("general/Export24"));
		generateButton.addActionListener(e -> withException(() -> generateCode(testCaseProperty.getValue())));

		final JButton runButton = new JButton(icon("media/Play24"));
		runButton.addActionListener(e -> withException(() -> {
			testRemoteControl.resetConnection();
			new Thread(() -> withException(() -> {
				final int port = testRemoteControl.prepare();
				LOGGER.log(Level.INFO, "Using port " + port);
				final TestCase testCase = testCaseProperty.getValue();
				final File file = generateCode(testCase);
				startTestCase(file, testCase.getFolder() + "." + testCase.getName(), port);
				testRemoteControl.start();
			}), "Test execution").start();
		}));

		final JButton resumeButton = new JButton(icon("media/StepForward24"));
		resumeButton.addActionListener(e -> withException(testRemoteControl::resume));

		final JSeparator sep = new JSeparator(JSeparator.VERTICAL);

		final JButton addStepButton = new JButton(icon("table/RowInsertAfter24"));
		addStepButton.addActionListener(e -> withException(this::addStep));

		final JButton removeStepButton = new JButton(icon("table/RowDelete24"));
		removeStepButton.addActionListener(e -> withException(this::removeStep));

		final StepsTable stepsTable = new StepsTable(testCaseProperty, selectedStep, testRemoteControl);
		this.getContentPane().add(stepsTable, BorderLayout.CENTER);

		final JToolBar buttons = new JToolBar();
		buttons.add(newTCButton);
		buttons.add(loadButton);
		buttons.add(saveButton);
		buttons.add(generateButton);
		buttons.add(runButton);
		buttons.add(resumeButton);
		buttons.add(sep);
		buttons.add(addStepButton);
		buttons.add(removeStepButton);
		this.getContentPane().add(buttons, BorderLayout.NORTH);

		changeSupport.attachAll();

		this.pack();
		this.setSize(1600, 1200);
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
		testCaseProperty.setValue(this, newTestCase);
	}

	public void addStep() {
		final TestCase testCase = testCaseProperty.getValue();

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
		testCaseProperty.forceChanged(this);
	}

	public void removeStep() {
		final TestCase testCase = testCaseProperty.getValue();
		if (testCase.getSteps().size() == 1) {
			return;
		}
		if (selectedStep.getValue() == null) {
			return;
		}
		testCase.getSteps().remove(selectedStep.getValue().getOrdinal() - 1);
		testCase.fixOrdinals();
		testCaseProperty.forceChanged(this);
	}

	public void loadTestCase(final TestCase testCase) {
		for (int i = 0; i < testCase.getSteps().size(); i++) {
			final TestStep testStep = testCase.getSteps().get(i);
			if (testStep.getOrdinal() != i + 1) {
				throw new IllegalStateException("Step " + i + ": wrong ordinal " + testStep.getOrdinal());
			}
		}
		testCaseProperty.setValue(this, testCase);
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
			Files.write(testFile.toPath(),
					JsonHelper.toJson(testCaseProperty.getValue()).getBytes(StandardCharsets.UTF_8),
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

	private interface EventWithException {

		void execute() throws Exception;

	}

	private void withException(final EventWithException e) {
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

	public void startTestCase(final File file, final String className, final int tcpPort)
			throws IOException, InterruptedException {

		final String currentClassPath = Arrays
				.stream(((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs())
				.map(Object::toString).collect(joining(":"));

		final Process testCompiler = new ProcessBuilder("javac", "-cp", currentClassPath, "-d",
				System.getProperty("java.io.tmpdir") + "/tc", file.toString()).redirectErrorStream(true).start();
		new StreamHandler(testCompiler.getInputStream(), LOGGER::info).start();
		if (testCompiler.waitFor() != 0) {
			throw new IllegalStateException("Compiler failed with status " + testCompiler.exitValue());
		}

		final Process runTest = new ProcessBuilder("java", "-cp", currentClassPath + ":/tmp/tc",
				"-Dtest.port=" + tcpPort, "-Dremote.controller=true", "org.junit.runner.JUnitCore", className)
						.redirectErrorStream(true).start();
		new StreamHandler(runTest.getInputStream(), LOGGER::info).start();
	}

}
