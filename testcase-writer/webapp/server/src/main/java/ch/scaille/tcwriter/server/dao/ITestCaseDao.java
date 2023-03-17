package ch.scaille.tcwriter.server.dao;

import java.util.List;
import java.util.Optional;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;

public interface ITestCaseDao {

	List<Metadata> listAll(TestDictionary dictionary);

	Optional<ExportableTestCase> load(String dictionaryName, TestDictionary dictionary);

}
