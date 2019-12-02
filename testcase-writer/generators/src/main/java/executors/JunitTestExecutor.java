package executors;

import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Logger;

import ch.skymarshall.tcwriter.generators.TestCaseToJava;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.util.helpers.ClassLoaderHelper;

public class JunitTestExecutor implements ITestExecutor {

	private static final Logger LOGGER = Logger.getLogger(JunitTestExecutor.class.getName());

	private static final Path tmp = new File(System.getProperty("java.io.tmpdir")).toPath();

	private static final String currentClassPath = classPath();

	private final Path junitTemplate;

	private final Path javaTargetPath;

	private final TestCaseToJava testCaseToJava;

	public JunitTestExecutor(final Path javaTemplate, final Path javaTargetPath) throws IOException {
		this.junitTemplate = javaTemplate;
		this.javaTargetPath = javaTargetPath;
		this.testCaseToJava = new TestCaseToJava(junitTemplate);
	}

	@Override
	public File generateCode(final TestCase tc) throws IOException, TestCaseException {
		return testCaseToJava.generateAndWrite(tc, javaTargetPath);
	}

	@Override
	public void compile(final File sourceFile) throws IOException, InterruptedException {
		final String waveClassPath = ClassLoaderHelper.appClassPath().stream()
				.filter(j -> j.toString().contains("testcase-writer") && j.toString().contains("annotations"))
				.map(URL::getFile).collect(joining(":"));
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
	}

	@Override
	public void execute(final String className, final int tcpPort) throws IOException {
		final Process runTest = new ProcessBuilder("java", "-cp", tmp.resolve("tc") + ":" + currentClassPath,
				"-Dtest.port=" + tcpPort, "-Dtc.stepping=true", "org.junit.runner.JUnitCore", className)
						.redirectErrorStream(true).start();
		new StreamHandler(runTest.getInputStream(), LOGGER::info).start();
	}

	public static String classPath() {
		return Arrays.stream(((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs())
				.map(URL::getFile).collect(joining(":"));
	}

	private class StreamHandler implements Runnable {

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

}
