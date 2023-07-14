package ch.scaille.tcwriter.testexec;

import static ch.scaille.util.helpers.LambdaExt.uncheckF2;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import ch.scaille.tcwriter.generators.TestCaseToJava;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.persistence.IConfigDao;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.FilesExt;
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
        this.config = configDao.getCurrentConfig().getSubconfig(JunitTestExecConfig.class).get();
    }

    @Override
    public String generateCode(TestCase tc) throws TestCaseException {
        return new TestCaseToJava(this.modelDao).generate(tc).writeTo(uncheckF2(this.modelDao::writeTestCaseCode));
    }

    @Override
    public String generateCodeLocal(TestConfig testConfig) throws IOException, TestCaseException {
        return new TestCaseToJava(this.modelDao).generate(testConfig.testCase).writeToFolder(testConfig.sourceFolder)
                .toString();
    }

    @Override
    public String compile(TestConfig testConfig) throws IOException, InterruptedException {
        final var aspectsClassPath = ClassLoaderHelper.cpToCommandLine(Stream.of(classPath)
                .filter(j -> j.toString().contains("testcase-writer")
                        && (j.toString().contains("api") || j.toString().contains("javatc")))
                .toArray(URL[]::new),  ClassLoaderHelper.cpToURLs(config.getClasspath()));
        final var testCompiler = exec("Compile", new String[]{config.getJava(), //
                "-cp", ClassLoaderHelper.cpToCommandLine(classPath), //
                "org.aspectj.tools.ajc.Main", //
                "-aspectpath", aspectsClassPath, //
                "-source", "11", //
                "-target", "11", //
                "-verbose", //
                // "-verbose", "-showWeaveInfo", //
                "-d", testConfig.binaryFolder.toString(), // 
                "-sourceroots", testConfig.sourceFolder.toString()});//
        if (testCompiler.waitFor() != 0) {
            throw new IllegalStateException("Compiler failed with status " + testCompiler.exitValue());
        }
        return testConfig.testCase.getPkgAndClassName();
    }

    @Override
    public void execute(TestConfig testConfig, String binaryRef) throws IOException {
        final var binaryURL = testConfig.binaryFolder;
        final var junit = Arrays.stream(classPath)
                .filter(s -> s.toString().contains("junit-platform-console-standalone")).findAny()
                .orElseThrow(() -> new IllegalStateException("junit-platform-console-standalone was not found"))
                .getFile();

        var parameters = new ArrayList<String>();
        parameters.addAll(Arrays.asList(config.getJava(), //
                "-Dtest.port=" + testConfig.tcpPort, "-Dtc.stepping=true", //
                "-jar", junit, "--select-class=" + binaryRef, "--details", "verbose"));
        parameters.addAll(toMultipleCommandLine(classPath));
        parameters.add("-cp=" + binaryURL);
        parameters.add("-cp=" + ClassLoaderHelper.cpToCommandLine(ClassLoaderHelper.cpToURLs(config.getClasspath())));
        exec("Execution", parameters.toArray(new String[0]));
    }

    private List<String> toMultipleCommandLine(URL[] classPath) {
        return Lists.partition(Arrays.asList(classPath), 50).stream()
                .map(c -> LambdaExt.uncheck(() -> "-cp=" + ClassLoaderHelper.cpToCommandLine(c.toArray(new URL[0]))))
                .toList();
    }

    private Process exec(String stage, String[] command) throws IOException {
        LOGGER.info(() -> stage + ' ' + String.join("\n", command));
        final var process = new ProcessBuilder(command).redirectErrorStream(true).start();
        FilesExt.streamHandler(process::getInputStream, LOGGER::info).start();
        return process;
    }
}
