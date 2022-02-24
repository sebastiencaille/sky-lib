package ch.scaille.tcwriter.executors;

import static ch.scaille.util.helpers.ClassLoaderHelper.cpToCommandLine;
import static ch.scaille.util.helpers.LambdaExt.uncheckF;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

import ch.scaille.tcwriter.generators.TestCaseToJava;
import ch.scaille.tcwriter.generators.model.TestCaseException;
import ch.scaille.tcwriter.generators.model.persistence.IModelDao;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.Logs;

public class JunitTestExecutor implements ITestExecutor {

	private static final Logger LOGGER = Logs.of(JunitTestExecutor.class);

	private final Path tempPath;

	private final URL[] classPath;

	private final IModelDao modelDao;

	public JunitTestExecutor(final IModelDao modelDao, final URL[] classPath) throws IOException {
		this.modelDao = modelDao;
		this.tempPath = Files.createTempDirectory("tcwriter");
		this.tempPath.toFile().deleteOnExit();
		this.classPath = classPath;
	}

	@Override
	public URI generateCode(TestCase tc) throws IOException, TestCaseException {
		return new TestCaseToJava(this.modelDao).generate(tc).writeTo(uncheckF(this.modelDao::exportTestCase));
	}

	@Override
	public Path generateCode(TestCase tc, Path targetFolder) throws IOException, TestCaseException {
		return new TestCaseToJava(this.modelDao).generate(tc).writeToFolder(targetFolder);
	}

	@Override
	public String compile(TestCase tc, final Path sourceFile) throws IOException, InterruptedException {
		final var waveClassPath = Stream.of(classPath)
				.filter(j -> j.toString().contains("testcase-writer") && j.toString().contains("annotations"))
				.map(URL::getFile).collect(joining(":"));
		final var testCompiler = new ProcessBuilder("java", //
				"-cp", ClassLoaderHelper.cpToCommandLine(classPath), //
				"org.aspectj.tools.ajc.Main", //
				"-aspectpath", waveClassPath, //
				"-source", "1.8", //
				"-target", "1.8", //
				// "-verbose", "-showWeaveInfo", //
				"-d", tempPath.toString(), //
				sourceFile.toString() //
		).redirectErrorStream(true).start();
		new StreamHandler(testCompiler::getInputStream, LOGGER::info).start();
		if (testCompiler.waitFor() != 0) {
			throw new IllegalStateException("Compiler failed with status " + testCompiler.exitValue());
		}
		return tc.getPackageAndClassName();
	}

	@Override
	public void execute(final String className, final int tcpPort) throws IOException {
		final var targetURL = tempPath.toUri().toURL();
		final var runTest = new ProcessBuilder("java", "-cp", cpToCommandLine(classPath, targetURL),
				"-Dtest.port=" + tcpPort, "-Dtc.stepping=true", "org.junit.runner.JUnitCore", className)
						.redirectErrorStream(true).start();
		new StreamHandler(runTest::getInputStream, LOGGER::info).start();
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
			try (var strIn = in.get()) {
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
