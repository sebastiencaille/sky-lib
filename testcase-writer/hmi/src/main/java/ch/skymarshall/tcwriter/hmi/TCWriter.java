package ch.skymarshall.tcwriter.hmi;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.RootListModel;
import org.skymarshall.hmi.model.views.ListViews;

import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.hmi.steps.StepsTable;

public abstract class TCWriter extends JFrame {

	private final ListModel<TestStep> steps = new RootListModel<>(ListViews.sorted(TestStep::getOrdinal));

	public abstract File generateCode(TestCase tc) throws TestCaseException, IOException;

	public TCWriter(final TestCase tc) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final TestRemoteControl testRemoteControl = new TestRemoteControl(9998);

		for (int i = 0; i < tc.getSteps().size(); i++) {
			final TestStep testStep = tc.getSteps().get(i);
			if (testStep.getOrdinal() != i + 1) {
				throw new IllegalStateException("Step " + i + ": wrong ordinal " + testStep.getOrdinal());
			}
		}

		this.getContentPane().setLayout(new BorderLayout());
		this.steps.addValues(tc.getSteps());

		final JButton generateButton = new JButton("Generate");
		generateButton.addActionListener(e -> {
			try {
				generateCode(tc);
			} catch (final TestCaseException | IOException e1) {
				throw new IllegalStateException("Unable to generate test case", e1);
			}
		});

		final JButton runButton = new JButton("Run");
		runButton.addActionListener(e -> {
			try {
				testRemoteControl.reset();
				final int port = testRemoteControl.prepare();
				System.out.println("Using port " + port);
				final File file = generateCode(tc);
				startTestCase(file, tc.getFolder() + "." + tc.getName(), port);
				testRemoteControl.start();
			} catch (final TestCaseException | IOException | InterruptedException e1) {
				throw new IllegalStateException("Unable to run test case", e1);
			}
		});

		final JButton resumeButton = new JButton("Resume");
		resumeButton.addActionListener(e -> {
			try {
				testRemoteControl.resume();
			} catch (final IOException e1) {
				throw new IllegalStateException("Unable to resume test case", e1);
			}
		});

		final StepsTable stepsTable = new StepsTable(steps, tc, testRemoteControl);
		this.getContentPane().add(stepsTable, BorderLayout.CENTER);
		final JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(generateButton);
		buttons.add(runButton);
		buttons.add(resumeButton);
		this.getContentPane().add(buttons, BorderLayout.SOUTH);

		this.pack();
		this.setSize(1600, 1200);
	}

	private static class StreamHandler implements Runnable {
		private final InputStream in;

		public StreamHandler(final InputStream in) {
			this.in = in;
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
					System.out.println(new String(buffer, 0, read));
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
				.map(cp -> cp.toString()).collect(Collectors.joining(":"));

		final Process testCompiler = new ProcessBuilder("javac", "-cp", currentClassPath, "-d", "/tmp/tc",
				file.toString()).redirectErrorStream(true).start();
		new StreamHandler(testCompiler.getInputStream()).start();
		if (testCompiler.waitFor() != 0) {
			throw new IllegalStateException("Compiler failed with status " + testCompiler.exitValue());
		}

		final Process runTest = new ProcessBuilder("java", "-cp", currentClassPath + ":/tmp/tc",
				"-Dtest.port=" + tcpPort, "org.junit.runner.JUnitCore", className).redirectErrorStream(true).start();
		new StreamHandler(runTest.getInputStream()).start();
	}

}
