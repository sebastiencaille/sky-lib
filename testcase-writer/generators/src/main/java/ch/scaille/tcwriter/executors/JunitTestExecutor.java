package ch.scaille.tcwriter.executors;

import static ch.scaille.util.helpers.ClassLoaderHelper.cpToCommandLine;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

import ch.scaille.tcwriter.generators.TestCaseToJava;
import ch.scaille.tcwriter.generators.model.TestCaseException;
import ch.scaille.tcwriter.generators.model.persistence.IModelPersister;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;
import ch.scaille.util.helpers.ClassLoaderHelper;

public class JunitTestExecutor implements ITestExecutor {

	private static final Logger LOGGER = Logger.getLogger(JunitTestExecutor.class.getName());

	private static final Path tmp = Paths.get(System.getProperty("java.io.tmpdir"));

	private final Path javaTargetPath;

	private final URL[] classPath;

	private IModelPersister persister;

	public JunitTestExecutor(final IModelPersister persister, final URL[] classPath) throws IOException {
		this.persister = persister;
		this.javaTargetPath = Files.createTempDirectory("tcwriter");
		this.javaTargetPath.toFile().deleteOnExit();
		this.classPath = classPath;
	}

	@Override
	public File generateCode(final TestCase tc) throws IOException, TestCaseException {
		TestCaseToJava testCaseToJava = new TestCaseToJava(persister);
		return testCaseToJava.generateAndWrite(tc, javaTargetPath);
	}

	@Override
	public void compile(final File sourceFile) throws IOException, InterruptedException {
		final String waveClassPath = Stream.of(classPath)
				.filter(j -> j.toString().contains("testcase-writer") && j.toString().contains("annotations"))
				.map(URL::getFile).collect(joining(":"));
		final Process testCompiler = new ProcessBuilder("java", //
				"-cp", ClassLoaderHelper.cpToCommandLine(classPath), //
				"org.aspectj.tools.ajc.Main", //
				"-aspectpath", waveClassPath, //
				"-source", "1.8", //
				"-target", "1.8", //
				// "-verbose", "-showWeaveInfo", //
				"-d", targetFolder().toString(), //
				sourceFile.toString() //
		).redirectErrorStream(true).start();
		new StreamHandler(testCompiler::getInputStream, LOGGER::info).start();
		if (testCompiler.waitFor() != 0) {
			throw new IllegalStateException("Compiler failed with status " + testCompiler.exitValue());
		}
	}

	@Override
	public void execute(final String className, final int tcpPort) throws IOException {
		final URL targetURL = targetFolder().toUri().toURL();
		final Process runTest = new ProcessBuilder("java", "-cp", cpToCommandLine(classPath, targetURL),
				"-Dtest.port=" + tcpPort, "-Dtc.stepping=true", "org.junit.runner.JUnitCore", className)
						.redirectErrorStream(true).start();
		new StreamHandler(runTest::getInputStream, LOGGER::info).start();
	}

	protected Path targetFolder() {
		return tmp.resolve("tc");
	}

	private class StreamHandler implements Runnable {

		private final Supplier<InputStream> in;
		private final Consumer<String> flow;

		public StreamHandler(final Supplier<InputStream> in, final Consumer<String> flow) {
			this.in = in;
			this.flow = flow;
		}

		public void start() {
			new Thread(this).start();
		}

		@Override
		public void run() {
			try (InputStream strIn = in.get()) {
				final byte[] buffer = new byte[1024 * 1024];
				int read;
				while ((read = strIn.read(buffer, 0, buffer.length)) >= 0) {
					flow.accept(new String(buffer, 0, read));
				}
			} catch (final IOException e) {
				// ignore
			}
		}
	}

}
