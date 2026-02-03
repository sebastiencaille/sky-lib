package ch.scaille.tcwriter.persistence;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.util.persistence.Resource;

public interface IModelDao {
	
	Template readTemplate(String templateName);


	// ----------------------- Dictionary -----------------------
	
	List<Metadata> listDictionaries();

	Optional<TestDictionary> readTestDictionary(String dictionaryName);

	void writeTestDictionary(TestDictionary testDictionary);

	// ----------------------- Test case -----------------------

	List<Metadata> listTestCases(final Metadata dictionary, Function<String, Metadata> metadataLoader);

	Optional<TestCase> readTestCase(String identifier, TestDictionary dictionary);

	void writeTestCase(String identifier, TestCase testCase);

	Resource<String> writeTestCaseCode(String identifier, String content);


}
