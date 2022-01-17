package ch.scaille.tcwriter.executors;

import java.io.IOException;
import java.nio.file.Path;

import ch.scaille.tcwriter.generators.model.TestCaseException;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;

public interface ITestExecutor {

	Path generateCode(TestCase tc) throws IOException, TestCaseException;

	void compile(Path sourceFile) throws IOException, InterruptedException;

	void execute(String className, int tcpPort) throws IOException;

	default void runTest(final TestCase tc, final int tcpPort)
			throws IOException, InterruptedException, TestCaseException {
		final Path sourceFile = generateCode(tc);
		compile(sourceFile);
		execute(tc.getPackageAndClassName(), tcpPort);
	}
}
