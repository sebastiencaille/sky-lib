package ch.scaille.tcwriter.executors;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.util.helpers.FilesExt;

public interface ITestExecutor {

	  class TestConfig implements AutoCloseable {

		public final TestCase testCase;

		public final int tcpPort;

		public final Path tmpFolder;
		public final Path sourceFolder;
		public final Path binaryFolder;

		public TestConfig(TestCase testCase, Path tmpFolder, int tcpPort) {
			this.tmpFolder = tmpFolder;
			this.testCase = testCase;
			this.tcpPort = tcpPort;
			sourceFolder = tmpFolder.resolve("src");
			binaryFolder = tmpFolder.resolve("bin");
		}

		@Override
		public void close() {
			FilesExt.removeFolderUnsafe(tmpFolder);
		}
	}

	URI generateCode(TestCase paramTestCase) throws IOException, TestCaseException;

	String generateCodeLocal(TestConfig config) throws IOException, TestCaseException;

	String compile(TestConfig config) throws IOException, InterruptedException;

	void execute(TestConfig config, String binaryRef) throws IOException;

	default void startTest(TestConfig config) throws IOException, InterruptedException, TestCaseException {
		Files.createDirectories(config.sourceFolder);
		Files.createDirectories(config.binaryFolder);
		generateCodeLocal(config);
		var binaryRef = compile(config);
		execute(config, binaryRef);
	}
}
