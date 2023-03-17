package ch.scaille.tcwriter.model.persistence;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.TestCase;

public interface IModelDao {
	
	Object getConfiguration();
	
	void saveConfiguration() throws IOException;

	Template readTemplate() throws IOException;


	// ----------------------- Dictionary -----------------------
	
	List<Metadata> listDictionaries() throws IOException;

	Optional<TestDictionary> readTestDictionary(String dictionaryName);

	void writeTestDictionary(TestDictionary testDictionary) throws IOException;

	void writeTestDictionary(Path path, TestDictionary testDictionary) throws IOException;

	// ----------------------- Test case -----------------------

	List<Metadata> listTestCases(TestDictionary dictionary) throws IOException;
	
	Optional<ExportableTestCase> readTestCase(String filename, TestDictionary testDictionary);

	void writeTestCase(String identifier, TestCase testCase) throws IOException;

	URI exportTestCase(String name, String content) throws IOException;


}
