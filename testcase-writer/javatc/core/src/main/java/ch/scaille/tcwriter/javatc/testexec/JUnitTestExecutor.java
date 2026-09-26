package ch.scaille.tcwriter.javatc.testexec;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.javatc.visitors.TestCaseToJavaVisitor;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.persistence.testexec.JunitTestExecConfig;
import ch.scaille.tcwriter.services.testexec.ITestExecutor;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.JavaExt;
import ch.scaille.util.helpers.LambdaExt;
import lombok.extern.java.Log;

@Log
public class JUnitTestExecutor implements ITestExecutor {

	private final IModelDao modelDao;

	private final JunitTestExecConfig config;

	private final URL aspectjWeaverPath;
	
	private final  URL junitJarFile;

	private final URL[] classPath;

	public JUnitTestExecutor(final IConfigDao configDao, final IModelDao modelDao, 
			URL aspectjWeaverPath, URL junitJarFile,
			final URL... classPath) {
		this.config = Objects.requireNonNull(configDao.getCurrentConfig()).getSubconfig(JunitTestExecConfig.class).orElseThrow();
		this.aspectjWeaverPath = aspectjWeaverPath;
		this.junitJarFile = junitJarFile;
		this.modelDao = modelDao;
		// The classpath contains the tcwriter communication framework 
		this.classPath = classPath;
	}


	public JUnitTestExecutor(final IConfigDao configDao, final IModelDao modelDao, final Path testClientJarPath) {
		this(configDao, modelDao,
				// aspect
				resolved(testClientJarPath, "aspectjweaver.jar"),
				// junit
				resolved(testClientJarPath, "junit-platform-console-standalone.jar"),
				// classpath
				resolved(testClientJarPath, "testcase-writer-javatc-recorder.jar"),
				resolved(testClientJarPath, "testcase-writer-javatc-client.jar"),
				resolved(testClientJarPath, "testcase-writer-api.jar"),
				resolved(testClientJarPath, "aspectjrt.jar")
		);
		
	}
	
	private static URL resolved(Path testClientJarPath, String jarName) {
		try {
			return testClientJarPath.resolve(jarName).toUri().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Unable to build url", e);
		}
		
	}

	
	@Override
	public Template createTemplate(TestCase tc) throws TestCaseException {
		final var generationMetadata = new GenerationMetadata(this.getClass(), tc.getName());
		return new TestCaseToJavaVisitor(this.modelDao.readTemplate(tc.getDictionary().template())).visitTestCase(tc, generationMetadata);
	}

	@Override
	public void write(TestConfig testConfig) throws IOException, TestCaseException {
		createTemplate(testConfig.testCase).writeToFolder(testConfig.sourceFolder);
	}

	@Override
	public String compile(TestConfig testConfig) throws IOException, InterruptedException {
		final var tcConfigClasspath = Arrays.stream(config.getClasspath())
				.map(LambdaExt.uncheckedF(cp -> Paths.get(cp).toUri().toURL()))
				.toArray(URL[]::new);
		final var parameters = new String[]{
				// TODO Config
				config.getJava() + 'c', //
				"-cp", ClassLoaderHelper.cpToCommandLine(classPath, tcConfigClasspath), //
				// TODO Cconfig
				"-source", "25", //
				"-target", "25", //
				"-d", testConfig.binaryFolder.toString(),
				testConfig.sourceFolder.resolve(testConfig.testCase.getPkgAndClassName().replace(".", "/") + ".java").toString()
			};
		final var testCompiler = exec("Compile", parameters);
		try {
			if (testCompiler.waitFor() != 0) {
				throw new IllegalStateException("Compiler failed with status " + testCompiler.exitValue());
			}
		} finally {
			//testCompiler.close();
		}
		return testConfig.testCase.getPkgAndClassName();
	}

	@Override
	public void execute(TestConfig testConfig, String binaryRef) throws IOException {
		final var binaryFolder = testConfig.binaryFolder;

		try (var aopTemplateStream = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("templates/aop.xml"), "aop template not found")) {
			final var aopTemplate = new String(aopTemplateStream.readAllBytes(), StandardCharsets.UTF_8);
			// TODO config
			Files.createDirectories(binaryFolder.resolve("META-INF"));
			Files.writeString(binaryFolder.resolve("META-INF/aop.xml"), aopTemplate.replace("<!-- WITHIN -->", "<include within=\"ch.scaille.tcwriter.examples\"/>"), StandardCharsets.UTF_8);
		}

		final var javaParameters = new ArrayList<String>();
		javaParameters.addAll(List.of(config.getJava(), //
				"-Dtest.port=" + testConfig.tcpPort, "-Dtc.stepping=true", //
				"-javaagent:" + ClassLoaderHelper.cpToCommandLine(new URL[] {aspectjWeaverPath}),
				"-jar",  ClassLoaderHelper.cpToCommandLine(new URL[]{ junitJarFile }), "execute", "--select-class=" + binaryRef, "--details", "verbose"));
		javaParameters.addAll(toMultipleCommandLine(classPath));
		javaParameters.add("-cp=" + binaryFolder);
		javaParameters.add("-cp=" + ClassLoaderHelper.cpToCommandLine(ClassLoaderHelper.cpToURLs(config.getClasspath())));
		new Thread(() -> LambdaExt.uncheck(() -> {
			final var process = exec("Execution", javaParameters.toArray(new String[0]));
			try {
				if (process.waitFor() != 0) {
					throw new IllegalStateException("Compiler failed with status " + process.exitValue());
				}
			} finally {
				//process.close();
			}
		})).start();
	}

	private List<String> toMultipleCommandLine(URL[] classPath) {
		return Lists.partition(List.of(classPath), 50)
				.stream()
				.map(cp -> LambdaExt.uncheck(() -> "-cp=" + ClassLoaderHelper.cpToCommandLine(cp.toArray(new URL[0]))))
				.toList();
	}

	private Process exec(String stage, String[] command) throws IOException {
		log.info(() -> stage + ' ' + String.join("\n", command));
		final var process = new ProcessBuilder(command).redirectErrorStream(true).start();
		JavaExt.inputStreamHandler(process::getInputStream, log::info).start();
		return process;
	}
}
