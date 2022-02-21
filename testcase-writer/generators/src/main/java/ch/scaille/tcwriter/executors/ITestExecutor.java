package ch.scaille.tcwriter.executors;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.scaille.tcwriter.generators.model.TestCaseException;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;

public interface ITestExecutor {

	URI generateCode(TestCase paramTestCase) throws IOException, TestCaseException;

	Path generateCode(TestCase paramTestCase, Path paramPath) throws IOException, TestCaseException;

	String compile(TestCase paramTestCase, Path paramPath) throws IOException, InterruptedException;

	void execute(String paramString, int paramInt) throws IOException;

	default void runTest(TestCase tc, int tcpPort) throws IOException, InterruptedException, TestCaseException {
		Path tmp = Files.createTempDirectory("tc");
		try {
			Path sourceFile = generateCode(tc, tmp);
			String className = compile(tc, sourceFile);
			execute(className, tcpPort);
		} finally {
			Files.delete(tmp);
		}
	}
}
