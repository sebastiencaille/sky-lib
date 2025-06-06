package ch.scaille.tcwriter.persistence;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.util.persistence.Resource;

public interface IModelDao {
	
	Template readTemplate();


	// ----------------------- Dictionary -----------------------
	
	List<Metadata> listDictionaries();

	Optional<TestDictionary> readTestDictionary(String dictionaryName);

	void writeTestDictionary(TestDictionary testDictionary);

	void writeTestDictionary(Path path, TestDictionary testDictionary);

	// ----------------------- Test case -----------------------

	List<Metadata> listTestCases(TestDictionary dictionary);
	
	Optional<ExportableTestCase> readTestCase(String identifier, Function<String, TestDictionary> testDictionaryLoader);

	void writeTestCase(String identifier, ExportableTestCase testCase);

	Resource<String> writeTestCaseCode(String identifier, String content);


}
