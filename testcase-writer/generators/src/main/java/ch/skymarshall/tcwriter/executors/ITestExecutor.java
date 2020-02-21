package ch.skymarshall.tcwriter.executors;

import java.io.File;
import java.io.IOException;

import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;

public interface ITestExecutor {

	File generateCode(TestCase tc) throws IOException, TestCaseException;

	void compile(File sourceFile) throws IOException, InterruptedException;

	void execute(String className, int tcpPort) throws IOException;

	default void runTest(final TestCase tc, final int tcpPort)
			throws IOException, InterruptedException, TestCaseException {
		final File sourceFile = generateCode(tc);
		compile(sourceFile);
		execute(tc.getPackageAndClassName(), tcpPort);
	}
}
