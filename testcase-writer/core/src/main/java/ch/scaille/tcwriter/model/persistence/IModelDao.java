package ch.scaille.tcwriter.model.persistence;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.model.testapi.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;

public interface IModelDao {
	Object getConfiguration();

	TestDictionary readTestDictionary() throws IOException;

	void writeTestDictionary(TestDictionary testDictionary) throws IOException;

	void writeTestDictionary(Path path, TestDictionary testDictionary) throws IOException;

	TestCase readTestCase(String filename, TestDictionary testDictionary) throws IOException;

	void writeTestCase(String identifier, TestCase testCase) throws IOException;

	IModelDao loadConfiguration(String identifier) throws IOException;

	void saveConfiguration() throws IOException;

	Template readTemplate() throws IOException;

	URI exportTestCase(String name, String content) throws IOException;
}
