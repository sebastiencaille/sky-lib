package ch.scaille.tcwriter.persistence.fsmodel;

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
import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.persistence.handlers.JsonModelDataHandler;
import ch.scaille.tcwriter.persistence.handlers.YamlModelDataHandler;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.persistence.IResourceRepository;
import ch.scaille.util.persistence.Resource;
import ch.scaille.util.persistence.StorageException;
import ch.scaille.util.persistence.StorageRTException;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public class FsModelDao implements IModelDao {

	private final IConfigDao configDao;

	private IResourceRepository<TestDictionary> dictionaryRepo;

	private IResourceRepository<ExportableTestCase> testCaseRepo;

	private IResourceRepository<String> templateRepo;

	private IResourceRepository<String> testCaseCodeRepo;

	private StorageDataHandlerRegistry serDeserializerRegistry;

	public static StorageDataHandlerRegistry defaultDataHandlers() {
		var modelSerDeserializerRegistry = new StorageDataHandlerRegistry(new YamlModelDataHandler());
		modelSerDeserializerRegistry.register(new JsonModelDataHandler());
		return modelSerDeserializerRegistry;
	}

	private static FsModelConfig configOf(TCConfig config) {
		return config.getSubconfig(FsModelConfig.class)
				.orElseThrow(() -> new IllegalStateException("Cannot find dao config"));
	}

	public FsModelDao(IConfigDao configLoader) {
		this(configLoader, defaultDataHandlers());
	}

	public FsModelDao(IConfigDao configLoader, StorageDataHandlerRegistry serDeserializerRegistry) {
		this.configDao = configLoader;
		this.serDeserializerRegistry = serDeserializerRegistry;
		this.configDao.onReload(c -> reload(configOf(c)));
	}

	private void reload(FsModelConfig config) {
		this.dictionaryRepo = configDao.loaderOf(TestDictionary.class, config.getDictionaryPath(),
				serDeserializerRegistry);
		this.testCaseRepo = configDao.loaderOf(ExportableTestCase.class, config.getTcPath(), serDeserializerRegistry);
		this.templateRepo = configDao.loaderOf(String.class, config.getTemplatePath(), null);
		this.testCaseCodeRepo = configDao.loaderOf(String.class, config.getTcExportPath(), null);
	}

	@Override
	public List<Metadata> listDictionaries() throws IOException {
		return StorageRTException.uncheck("Listing of dictionaries",
				() -> dictionaryRepo.list().map(f -> readTestDictionary(f).get().getMetadata()).toList());
	}

	@Override
	public void writeTestDictionary(TestDictionary tm) {
		var id = tm.getMetadata().getTransientId();
		if (id.isEmpty()) {
			id = "default";
		}
		final var idSafe = id;
		StorageRTException.uncheck("Writing of test dictionary", () -> dictionaryRepo.write(idSafe, tm));
	}

	@Override
	public Optional<TestDictionary> readTestDictionary(String dictionaryId) {
		try {
			final var dictionary = dictionaryRepo.read(dictionaryId);
			dictionary.getMetadata().setTransientId(dictionaryId);
			return Optional.of(dictionary);
		} catch (StorageException e) {
			Logs.of(FsModelDao.class).log(Level.WARNING, e, () -> "Unable to load dictionary");
			return Optional.empty();
		}
	}

	@Override
	public void writeTestDictionary(Path target, TestDictionary tm) {
		StorageRTException.uncheck("Writing of test dictionary", () -> dictionaryRepo.write(target.toString(), tm));
	}

	@Override
	public List<Metadata> listTestCases(final TestDictionary dictionary) {
		return StorageRTException.uncheck("Listing of test cases",
				() -> testCaseRepo.list().map(f -> readTestCase(f, dictionary).get().getMetadata()).toList());
	}

	@Override
	public Optional<ExportableTestCase> readTestCase(String locator, TestDictionary testDictionary) {
		try {
			final var testCase = testCaseRepo.read(locator);
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
		StorageRTException.uncheck("Writing of test case", () -> testCaseRepo.write(locator, (ExportableTestCase) tc));
	}

	@Override
	public Template readTemplate() {
		return StorageRTException.uncheck("Reading of template", () -> new Template(templateRepo.readRaw("")));
	}

	@Override
	public Resource writeTestCaseCode(String locator, String code) {
		return StorageRTException.uncheck("Writing of test case code", () -> testCaseCodeRepo.writeRaw(locator, code));
	}

	public static String toIdentifier(Path path) {
		return path.getFileName().toString().replace(".json", "");
	}

	public Path getTCFolder() {
		return Path.of(configOf(configDao.getCurrentConfig()).getTcPath());
	}

}
