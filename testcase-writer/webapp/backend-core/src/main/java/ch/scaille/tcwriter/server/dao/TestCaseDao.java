package ch.scaille.tcwriter.server.dao;

import java.util.List;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.util.persistence.DaoFactory;

public class TestCaseDao extends AbstractDao implements ITestCaseDao {


    public TestCaseDao(IModelDao modelDao) {
       super(modelDao);
    }

    @Override
    public List<Metadata> listAll(Metadata dictionary) {
        return modelDao.listTestCases(dictionary);
    }

    @Override
    public Metadata loadMetadata(String testCaseName) {
        return modelDao.loadTestCaseMetadata(testCaseName);
    }

    @Override
    public TestCase load(String testCaseName, TestDictionary dictionary) {
        return modelDao.readTestCase(testCaseName, dictionary).orElse(null);
    }

    @Override
    public void save(TestCase testCase) {
        modelDao.writeTestCase(testCase.getName(), testCase);
    }
}
