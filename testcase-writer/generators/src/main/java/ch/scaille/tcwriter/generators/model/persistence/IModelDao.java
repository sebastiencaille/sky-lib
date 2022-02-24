package ch.scaille.tcwriter.generators.model.persistence;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public interface IModelDao {
	Object getConfiguration();

	TestDictionary readTestDictionary() throws IOException;

	void writeTestDictionary(TestDictionary paramTestDictionary) throws IOException;

	void writeTestDictionary(Path paramPath, TestDictionary paramTestDictionary) throws IOException;

	TestCase readTestCase(String paramString, TestDictionary paramTestDictionary) throws IOException;

	void writeTestCase(String paramString, TestCase paramTestCase) throws IOException;

	IModelDao loadConfiguration(String paramString) throws IOException;

	void saveConfiguration() throws IOException;

	Template readTemplate() throws IOException;

	URI exportTestCase(String paramString1, String paramString2) throws IOException;
}
