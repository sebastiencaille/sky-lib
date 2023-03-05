package ch.scaille.tcwriter.executors;

import static ch.scaille.util.helpers.ClassLoaderHelper.cpToCommandLine;
import static ch.scaille.util.helpers.LambdaExt.uncheckF2;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Stream;

import ch.scaille.tcwriter.generators.TestCaseToJava;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.FilesExt;
import ch.scaille.util.helpers.Logs;

public class JUnitTestExecutor implements ITestExecutor {

	private static final Logger LOGGER = Logs.of(JUnitTestExecutor.class);

	private final URL[] classPath;

	private final IModelDao modelDao;

	private final String java;

	public JUnitTestExecutor(final IModelDao modelDao, final URL[] classPath) {
		this.modelDao = modelDao;
		this.classPath = classPath;
		java = System.getProperty("java.home") + "/bin/java";
	}

	@Override
	public URI generateCode(TestCase tc) throws IOException, TestCaseException {
		return new TestCaseToJava(this.modelDao).generate(tc).writeTo(uncheckF2(this.modelDao::exportTestCase));
	}

	@Override
	public String generateCodeLocal(ExecConfig config) throws IOException, TestCaseException {
		return new TestCaseToJava(this.modelDao).generate(config.testCase).writeToFolder(config.sourceFolder)
				.toString();
	}

	@Override
	public String compile(ExecConfig config, String sourceRef) throws IOException, InterruptedException {
		final var aspectsClassPath = Stream.of(classPath)
				.filter(j -> j.toString().contains("testcase-writer") &&
						(j.toString().contains("api") || j.toString().contains("javatc")))
				.map(URL::getFile).collect(joining(":"));
		final var testCompiler = exec("Compile", new String[] { java, //
				"-cp", ClassLoaderHelper.cpToCommandLine(classPath), //
				"org.aspectj.tools.ajc.Main", //
				"-aspectpath", aspectsClassPath, //
				"-source", "11", //
				"-target", "11", //
				// "-verbose", "-showWeaveInfo", //
				"-d", config.binaryFolder.toString(), //
				"-sourceroots", config.sourceFolder.toString() });//
		if (testCompiler.waitFor() != 0) {
			throw new IllegalStateException("Compiler failed with status " + testCompiler.exitValue());
		}
		return config.testCase.getPkgAndClassName();
	}

	@Override
	public void start(ExecConfig config, String binaryRef) throws IOException {
		final var binaryURL = config.binaryFolder;
		final var junit = Arrays.asList(classPath).stream()
				.filter(s -> s.toString().contains("junit-platform-console-standalone")).findAny()
				.orElseThrow(() -> new IllegalStateException("junit-platform-console-standalone was not found"))
				.getFile();

		exec("Execution", new String[]{java, // 
				"-cp", cpToCommandLine(classPath), //
				"-Dtest.port=" + config.tcpPort, "-Dtc.stepping=true", // 
				"-jar", junit, "--select-class=" + binaryRef,
				"-cp=" + cpToCommandLine(classPath, binaryURL.toUri().toURL())});
	
	}

	private Process exec(String stage, String[] command) throws IOException {
		LOGGER.info(() -> stage + ' ' + String.join("\n", command));
		final var process = new ProcessBuilder(command).redirectErrorStream(true).start();
		FilesExt.streamHandler(process::getInputStream, LOGGER::info).start();
		return process;
	}
}
