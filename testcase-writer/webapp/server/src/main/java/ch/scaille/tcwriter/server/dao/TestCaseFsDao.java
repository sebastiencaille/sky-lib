package ch.scaille.tcwriter.server.dao;

import static ch.scaille.util.helpers.LambdaExt.raise;
import static ch.scaille.util.helpers.LambdaExt.uncheckF;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import ch.scaille.tcwriter.generators.TCConfig;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.util.exceptions.StorageRTException;
import jakarta.annotation.PostConstruct;

public class TestCaseFsDao implements TestCaseDao {

	private final Map<String, ExportableTestCase> cache = new HashMap<>();

	@Value("${app.dataFolder:/var/lib/tcwriter/data}")
	private String dataFolder;

	private FsModelDao modelDao;

	@PostConstruct
	private void created() {
		final var config = new TCConfig();
		config.setName("server");
		config.setBase(dataFolder);
		modelDao = new FsModelDao(config);
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
