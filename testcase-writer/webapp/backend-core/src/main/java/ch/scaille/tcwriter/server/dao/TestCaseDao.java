package ch.scaille.tcwriter.server.dao;

import java.util.List;
import java.util.Optional;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.persistence.IModelDao;

public class TestCaseDao extends AbstractDao implements ITestCaseDao {

    private final IModelDao modelDao;

    public TestCaseDao(IModelDao modelDao) {
        this.modelDao = modelDao;
    }

    @Override
    public List<Metadata> listAll(TestDictionary dictionary) {
        return modelDao.listTestCases(dictionary);
    }

    @Override
    public Optional<ExportableTestCase> load(String testCaseName, TestDictionary dictionary) {
        return modelDao.readTestCase(testCaseName, preferred -> dictionary);
    }

}
