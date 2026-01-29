package ch.scaille.tcwriter.services.testexec;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.generators.services.visitors.TestCaseToJunitVisitor;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.persistence.testexec.JunitTestExecConfig;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.JavaExt;
import ch.scaille.util.helpers.LambdaExt;
import ch.scaille.util.helpers.Logs;

public class JUnitTestExecutor implements ITestExecutor {

	private static final Logger LOGGER = Logs.of(JUnitTestExecutor.class);
 
	private final URL[] classPath;

	private final IModelDao modelDao;

	private final JunitTestExecConfig config;

	public JUnitTestExecutor(final IConfigDao configDao, final IModelDao modelDao, final URL[] classPath) {
		this.modelDao = modelDao;
		this.classPath = classPath;
		this.config = configDao.getCurrentConfig().getSubconfig(JunitTestExecConfig.class).orElseThrow();
	}

	@Override
	public Template createTemplate(TestCase tc) throws TestCaseException {
		final var generationMetadata = new GenerationMetadata(this.getClass(), tc.getName());
		return new TestCaseToJunitVisitor(this.modelDao.readTemplate()).visitTestCase(tc, generationMetadata);
	}

	@Override
	public String write(TestConfig testConfig) throws IOException, TestCaseException {
		return createTemplate(testConfig.testCase).writeToFolder(testConfig.sourceFolder).toString();
	}

	@Override
	public String compile(TestConfig testConfig) throws IOException, InterruptedException {
		final var aspectsClassPath = ClassLoaderHelper.cpToCommandLine(Stream.of(classPath)
				.filter(j -> j.toString().contains("testcase-writer")
						&& (j.toString().contains("api") || j.toString().contains("javatc")))
				.toArray(URL[]::new), ClassLoaderHelper.cpToURLs(config.getClasspath()));
		final var testCompiler = exec("Compile", new String[] { config.getJava(), //
				"-cp", ClassLoaderHelper.cpToCommandLine(classPath), //
				"org.aspectj.tools.ajc.Main", //
				"-aspectpath", aspectsClassPath, //
 				// TODO config
				"-source", "25", //
				"-target", "25", //
				"-verbose", //
				// "-verbose", "-showWeaveInfo", //
				"-d", testConfig.binaryFolder.toString(), //
				"-sourceroots", testConfig.sourceFolder.toString() });//
		if (testCompiler.waitFor() != 0) {
			throw new IllegalStateException("Compiler failed with status " + testCompiler.exitValue());
		}
		return testConfig.testCase.getPkgAndClassName();
	}

	@Override
	public void execute(TestConfig testConfig, String binaryRef) throws IOException {

		final var binaryURL = testConfig.binaryFolder;
		final var junitJarFile = Arrays.stream(classPath)
				.filter(s -> s.toString().contains("junit-platform-console-standalone"))
				.findAny()
				.orElseThrow(() -> new IllegalStateException("junit-platform-console-standalone was not found"));

		final var javaParameters = new ArrayList<String>();
		javaParameters.addAll(List.of(config.getJava(), //
				"-Dtest.port=" + testConfig.tcpPort, "-Dtc.stepping=true", //
				"-jar",  ClassLoaderHelper.cpToCommandLine(new URL[]{ junitJarFile }), "execute", "--select-class=" + binaryRef, "--details", "verbose"));
		javaParameters.addAll(toMultipleCommandLine(classPath));
		javaParameters.add("-cp=" + binaryURL);
		javaParameters.add("-cp=" + ClassLoaderHelper.cpToCommandLine(ClassLoaderHelper.cpToURLs(config.getClasspath())));
		exec("Execution", javaParameters.toArray(new String[0]));
	}

	private List<String> toMultipleCommandLine(URL[] classPath) {
		return Lists.partition(List.of(classPath), 50)
				.stream()
				.map(cp -> LambdaExt.uncheck(() -> "-cp=" + ClassLoaderHelper.cpToCommandLine(cp.toArray(new URL[0]))))
				.toList();
	}

	private Process exec(String stage, String[] command) throws IOException {
		LOGGER.info(() -> stage + ' ' + String.join("\n", command));
		final var process = new ProcessBuilder(command).redirectErrorStream(true).start();
		JavaExt.inputStreamHandler(process::getInputStream, LOGGER::info).start();
		return process;
	}
}
