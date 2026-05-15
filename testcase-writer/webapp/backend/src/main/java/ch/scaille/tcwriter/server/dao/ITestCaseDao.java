package ch.scaille.tcwriter.server.dao;

import java.util.List;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import org.jspecify.annotations.Nullable;

public interface ITestCaseDao {

	List<Metadata> listAll(Metadata dictionary);

	Metadata loadMetadata(String testCaseName);

	@Nullable
	TestCase load(String testCaseName, TestDictionary dictionary);

    void save(TestCase testCase);
}
