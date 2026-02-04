package ch.scaille.tcwriter.persistence;

import java.util.List;
import java.util.Optional;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.util.persistence.Resource;

public interface IModelDao {

	Template readTemplate(String templateName);


	// ----------------------- Dictionary -----------------------

	Metadata loadDictionaryMetadata(String locator);

	List<Metadata> listDictionaries(Metadata filter);

	Optional<TestDictionary> readTestDictionary(String dictionaryName);

	void writeTestDictionary(TestDictionary testDictionary);

	// ----------------------- Test case -----------------------

	Metadata loadTestCaseMetadata(String locator);

	List<Metadata> listTestCases(final Metadata dictionary);

	Optional<TestCase> readTestCase(String identifier, TestDictionary dictionary);

	void writeTestCase(String identifier, TestCase testCase);

	Resource<String> writeTestCaseCode(String identifier, String content);

}
