package ch.scaille.tcwriter.persistence;

import static ch.scaille.util.persistence.StorageRTException.uncheck;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.persistence.fs.FsModelDao;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.Resource;
import ch.scaille.util.persistence.StorageException;

public abstract class AbstractModelDao implements IModelDao {
	
	protected final IConfigDao configDao;
	
	protected IDao<TestDictionary> dictionaryRepo;

	protected IDao<ExportableTestCase> testCaseRepo;

	protected IDao<String> templateRepo;

	protected IDao<String> testCaseCodeRepo;

	protected abstract void reload(TCConfig config);
	
	protected AbstractModelDao(IConfigDao configDao) {
		this.configDao = configDao;
		this.configDao.getCurrentConfigProperty().listen(this::reload);
	}
	
	protected void reload() {
		reload(this.configDao.getCurrentConfig());
	}
	
	@Override
	public List<Metadata> listDictionaries() throws IOException {
		return uncheck("Listing of dictionaries",
				() -> dictionaryRepo.list().map(f -> readTestDictionary(f.getLocator()).get().getMetadata()).toList());
	}

	@Override
	public void writeTestDictionary(TestDictionary tm) {
		var id = tm.getMetadata().getTransientId();
		if (id.isEmpty()) {
			id = "default";
		}
		final var idSafe = id;
		uncheck("Writing of test dictionary", () -> dictionaryRepo.saveOrUpdate(idSafe, tm));
	}

	@Override
	public Optional<TestDictionary> readTestDictionary(String dictionaryId) {
		try {
			final var dictionary = dictionaryRepo.load(dictionaryId);
			dictionary.getMetadata().setTransientId(dictionaryId);
			return Optional.of(dictionary);
		} catch (StorageException e) {
			Logs.of(FsModelDao.class).log(Level.WARNING, e, () -> "Unable to load dictionary");
			return Optional.empty();
		}
	}

	@Override
	public void writeTestDictionary(Path target, TestDictionary tm) {
		uncheck("Writing of test dictionary", () -> dictionaryRepo.saveOrUpdate(target.toString(), tm));
	}

	@Override
	public List<Metadata> listTestCases(final TestDictionary dictionary) {
		return uncheck("Listing of test cases",
				() -> testCaseRepo.list().map(f -> readTestCase(f.getLocator(), dictionary).get().getMetadata()).toList());
	}

	@Override
	public Optional<ExportableTestCase> readTestCase(String locator, TestDictionary testDictionary) {
		try {
			final var testCase = testCaseRepo.load(locator);
			testCase.setDictionary(testDictionary);
			testCase.getMetadata().setTransientId(locator);
			testCase.restoreReferences();
			return Optional.of(testCase);
		} catch (StorageException e) {
			Logs.of(FsModelDao.class).log(Level.WARNING, e, () -> "Unable to load test case " + locator);
			return Optional.empty();
		}
	}

	@Override
	public void writeTestCase(String locator, TestCase tc) {
		uncheck("Writing of test case", () -> testCaseRepo.saveOrUpdate(locator, (ExportableTestCase) tc));
	}

	@Override
	public Template readTemplate() {
		return uncheck("Reading of template", () -> new Template(templateRepo.load("")));
	}

	@Override
	public Resource<String> writeTestCaseCode(String locator, String code) {
		return uncheck("Writing of test case code", () -> testCaseCodeRepo.saveOrUpdate(locator, code));
	}

}
