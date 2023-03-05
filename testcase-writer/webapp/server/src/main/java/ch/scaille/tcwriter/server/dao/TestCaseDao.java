package ch.scaille.tcwriter.server.dao;

import static ch.scaille.util.helpers.LambdaExt.raise;
import static ch.scaille.util.helpers.LambdaExt.uncheckF;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.util.exceptions.StorageRTException;

public class TestCaseDao implements ITestCaseDao {

	private final Map<String, ExportableTestCase> cache = new HashMap<>();

	private final IModelDao modelDao;

	public TestCaseDao(IModelDao modelDao) {
		this.modelDao = modelDao;
	}
	
	@Override
	public List<Metadata> listAll(TestDictionary dictionary) {
		try {
			return modelDao.listTestCases(dictionary);
		} catch (IOException e) {
			throw new StorageRTException("Unable to list dictionaries", e);
		}
	}

	@Override
	public ExportableTestCase load(String testCaseName, TestDictionary dictionary) {
		return cache.computeIfAbsent(testCaseName + dictionary.getMetadata().getTransientId(),
				uncheckF(n -> modelDao.readTestCase(testCaseName, dictionary),
						raise((t, e) -> new StorageRTException("bad.testcase.folder", e))));
	}

}
