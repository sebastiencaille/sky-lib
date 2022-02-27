package ch.scaille.tcwriter.executors;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.scaille.tcwriter.generators.model.TestCaseException;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;
import ch.scaille.util.helpers.FilesExt;

public interface ITestExecutor {

	public static class ExecConfig {

		public final TestCase testCase;

		public final int tcpPort;

		public final Path tmpFolder;
		public final Path sourceFolder;
		public final Path binaryFolder;

		public ExecConfig(TestCase testCase, Path tmpFolder, int tcpPort) {
			this.tmpFolder = tmpFolder;
			this.testCase = testCase;
			this.tcpPort = tcpPort;
			sourceFolder = tmpFolder.resolve("src");
			binaryFolder = tmpFolder.resolve("bin");
		}

		public void clean() {
			FilesExt.removeFolderUnsafe(tmpFolder);
		}
	}

	URI generateCode(TestCase paramTestCase) throws IOException, TestCaseException;

	String generateCodeLocal(ExecConfig config) throws IOException, TestCaseException;

	String compile(ExecConfig config, String sourceRef) throws IOException, InterruptedException;

	void start(ExecConfig config, String binaryRef) throws IOException;

	default ExecConfig startTest(ExecConfig config) throws IOException, InterruptedException, TestCaseException {
		Files.createDirectories(config.sourceFolder);
		Files.createDirectories(config.binaryFolder);
		var sourceRef = generateCodeLocal(config);
		var binaryRef = compile(config, sourceRef);
		start(config, binaryRef);
		return config;
	}
}
