package ch.scaille.tcwriter.server.dao;

import java.util.List;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;

public interface TestCaseDao {

	List<Metadata> listAll(TestDictionary dictionary);

	ExportableTestCase load(String dictionaryName, TestDictionary dictionary);

}
