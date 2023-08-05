package ch.scaille.tcwriter.persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.util.persistence.Resource;

public interface IModelDao {
	
	Template readTemplate();


	// ----------------------- Dictionary -----------------------
	
	List<Metadata> listDictionaries() throws IOException;

	Optional<TestDictionary> readTestDictionary(String dictionaryName);

	void writeTestDictionary(TestDictionary testDictionary);

	void writeTestDictionary(Path path, TestDictionary testDictionary);

	// ----------------------- Test case -----------------------

	List<Metadata> listTestCases(TestDictionary dictionary) throws IOException;
	
	Optional<ExportableTestCase> readTestCase(String filename, TestDictionary testDictionary);

	void writeTestCase(String identifier, TestCase testCase);

	Resource writeTestCaseCode(String identifier, String content);


}
