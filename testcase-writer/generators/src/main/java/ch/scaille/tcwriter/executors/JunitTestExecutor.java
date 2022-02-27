package ch.scaille.tcwriter.executors;

import static ch.scaille.util.helpers.ClassLoaderHelper.cpToCommandLine;
import static ch.scaille.util.helpers.LambdaExt.uncheckF;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Stream;

import ch.scaille.tcwriter.generators.TestCaseToJava;
import ch.scaille.tcwriter.generators.model.TestCaseException;
import ch.scaille.tcwriter.generators.model.persistence.IModelDao;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.FilesExt;
import ch.scaille.util.helpers.Logs;

public class JunitTestExecutor implements ITestExecutor {

	private static final Logger LOGGER = Logs.of(JunitTestExecutor.class);

	private final URL[] classPath;

	private final IModelDao modelDao;

	private final String java;

	public JunitTestExecutor(final IModelDao modelDao, final URL[] classPath) {
		this.modelDao = modelDao;
		this.classPath = classPath;
		java = System.getProperty("java.home") + "/bin/java";
	}

	@Override
	public URI generateCode(TestCase tc) throws IOException, TestCaseException {
		return new TestCaseToJava(this.modelDao).generate(tc).writeTo(uncheckF(this.modelDao::exportTestCase));
	}

	@Override
	public String generateCodeLocal(ExecConfig config) throws IOException, TestCaseException {
		return new TestCaseToJava(this.modelDao).generate(config.testCase).writeToFolder(config.sourceFolder)
				.toString();
	}

	@Override
	public String compile(ExecConfig config, String sourceRef) throws IOException, InterruptedException {
		final var waveClassPath = Stream.of(classPath)
				.filter(j -> j.toString().contains("testcase-writer") && j.toString().contains("annotations"))
				.map(URL::getFile).collect(joining(":"));
		final var testCompiler = new ProcessBuilder(java, //
				"-cp", ClassLoaderHelper.cpToCommandLine(classPath), //
				"org.aspectj.tools.ajc.Main", //
				"-aspectpath", waveClassPath, //
				"-source", "11", //
				"-target", "11", //
				// "-verbose", "-showWeaveInfo", //
				"-d", config.binaryFolder.toString(), //
				"-sourceroots", config.sourceFolder.toString() //
		).redirectErrorStream(true).start();
		FilesExt.streamHandler(testCompiler::getInputStream, LOGGER::info).start();
		if (testCompiler.waitFor() != 0) {
			throw new IllegalStateException("Compiler failed with status " + testCompiler.exitValue());
		}
		return config.testCase.getPackageAndClassName();
	}

	@Override
	public void start(ExecConfig config, String binaryRef) throws IOException {
		final var binaryURL = config.binaryFolder;
		final var junit = Arrays.asList(classPath).stream()
				.filter(s -> s.toString().contains("junit-platform-console-standalone")).findAny()
				.orElseThrow(() -> new IllegalStateException("junit-platform-console-standalone was not found"))
				.getFile(); 

		final var runTest = new ProcessBuilder(java, "-cp", cpToCommandLine(classPath), "-Dtest.port=" + config.tcpPort,
				"-Dtc.stepping=true", "-jar", junit, "--select-class=" + binaryRef, "-cp=" + cpToCommandLine(classPath, binaryURL.toUri().toURL()))
						.redirectErrorStream(true).start();
		FilesExt.streamHandler(runTest::getInputStream, LOGGER::info).start();
	}

}
