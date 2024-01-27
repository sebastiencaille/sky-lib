package ch.scaille.tcwriter.persistence;

import static ch.scaille.util.persistence.StorageRTException.uncheck;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import ch.scaille.generators.util.Template;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.persistence.factory.DaoFactory;
import ch.scaille.tcwriter.persistence.handlers.JsonModelDataHandler;
import ch.scaille.tcwriter.persistence.handlers.YamlModelDataHandler;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.Resource;
import ch.scaille.util.persistence.StorageException;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import ch.scaille.util.persistence.handlers.TextStorageHandler;

public class ModelDao implements IModelDao {

	public static StorageDataHandlerRegistry defaultDataHandlers() {
		final var modelSerDeserializerRegistry = new StorageDataHandlerRegistry(new YamlModelDataHandler());
		modelSerDeserializerRegistry.register(new JsonModelDataHandler());
		modelSerDeserializerRegistry.register(new TextStorageHandler());
		return modelSerDeserializerRegistry;
	}

	private static ModelConfig configOf(TCConfig config) {
		return config.getSubconfig(ModelConfig.class)
				.orElseThrow(() -> new IllegalStateException("Cannot find FsModelConfig"));
	}

	private final ObjectProperty<TCConfig> config;

	private final StorageDataHandlerRegistry serDeserializerRegistry;

	private final DaoFactory daoFactory;

	protected IDao<TestDictionary> dictionaryRepo;

	protected IDao<ExportableTestCase> testCaseRepo;

	protected IDao<String> templateRepo;

	protected IDao<String> testCaseCodeRepo;

	public ModelDao(DaoFactory daoFactory, ObjectProperty<TCConfig> config,
			StorageDataHandlerRegistry serDeserializerRegistry) {
		this.daoFactory = daoFactory;
		this.config = config;
		this.serDeserializerRegistry = serDeserializerRegistry;
		this.config.listen(this::reload);
		reload(this.config.getValue());
	}

	protected void reload(TCConfig config) {
		if (config == null) {
			return;
		}
		final var modelConfig = configOf(config);
		this.dictionaryRepo = daoFactory.loaderOf(TestDictionary.class, modelConfig.getDictionaryPath(),
				serDeserializerRegistry);
		this.testCaseRepo = daoFactory.loaderOf(ExportableTestCase.class, modelConfig.getTcPath(),
				serDeserializerRegistry);
		this.templateRepo = daoFactory.loaderOf(String.class, modelConfig.getTemplatePath(), serDeserializerRegistry);
		this.testCaseCodeRepo = daoFactory.loaderOf(String.class, modelConfig.getTcExportPath(),
				serDeserializerRegistry);
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
			Logs.of(ModelDao.class).log(Level.WARNING, e, () -> "Unable to load dictionary");
			return Optional.empty();
		}
	}

	@Override
	public void writeTestDictionary(Path path, TestDictionary tm) {
		uncheck("Writing of test dictionary", () -> dictionaryRepo.saveOrUpdate(path.toString(), tm));
	}

	@Override
	public List<Metadata> listTestCases(final TestDictionary dictionary) {
		return uncheck("Listing of test cases",
				() -> testCaseRepo.list()
						.map(f -> readTestCase(f.getLocator(), dictionary).get().getMetadata())
						.toList());
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
			Logs.of(ModelDao.class).log(Level.WARNING, e, () -> "Unable to load test case " + locator);
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

	public ModelConfig getCurrentConfig() {
		return configOf(config.getValue());
	}

}
