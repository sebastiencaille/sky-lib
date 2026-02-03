package ch.scaille.tcwriter.server.dao;

import java.util.List;
import java.util.Optional;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.server.config.PersistenceConfig;
import ch.scaille.util.persistence.DaoFactory;

public class TestCaseDao extends AbstractDao implements ITestCaseDao {

    private final IModelDao modelDao;

    public TestCaseDao(IModelDao modelDao, DaoFactory.IDataSourceFactory factory) {
        super(factory);
        this.modelDao = modelDao;
    }

    @Override
    public List<Metadata> listAll(Metadata dictionary) {
        return modelDao.listTestCases(dictionary, this::loadMetadata);
    }

    @Override
    public Metadata loadMetadata(String testCaseName) {
        return super.loadMetadata(testCaseName, locator -> load(locator, TestDictionary.NOT_SET).getMetadata()).get();
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
