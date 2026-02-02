package ch.scaille.tcwriter.persistence;

import static ch.scaille.util.persistence.StorageRTException.uncheck;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

import ch.scaille.generators.util.Template;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.persistence.handlers.JsonModelDataHandler;
import ch.scaille.tcwriter.persistence.handlers.TemplateStorageHandler;
import ch.scaille.tcwriter.persistence.handlers.YamlModelDataHandler;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.persistence.DaoFactory;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.Resource;
import ch.scaille.util.persistence.StorageException;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import ch.scaille.util.persistence.handlers.TextStorageHandler;

public class ModelDao implements IModelDao {

	public static StorageDataHandlerRegistry defaultDataHandlers(IModelDao modelDao) {
		final var modelSerDeserializerRegistry = new StorageDataHandlerRegistry(new YamlModelDataHandler(modelDao));
		modelSerDeserializerRegistry.register(new JsonModelDataHandler(modelDao));
		modelSerDeserializerRegistry.register(new TextStorageHandler());
		modelSerDeserializerRegistry.register(new TemplateStorageHandler());
		return modelSerDeserializerRegistry;
	}

	private static ModelConfig configOf(TCConfig config) {
		return config.getSubconfig(ModelConfig.class)
				.orElseThrow(() -> new IllegalStateException("Cannot find FsModelConfig"));
	}

	private final ObjectProperty<TCConfig> config;

	private final StorageDataHandlerRegistry serDeserializerRegistry;

	private final StorageDataHandlerRegistry templateRegistry = new StorageDataHandlerRegistry(new TemplateStorageHandler());

	private final StorageDataHandlerRegistry testCaseCodeRegistry = new StorageDataHandlerRegistry(new TextStorageHandler());

	private final DaoFactory daoFactory;

	protected IDao<TestDictionary> dictionaryRepo;

	protected IDao<TestCase> testCaseRepo;

	protected IDao<String> templateRepo;

	protected IDao<String> testCaseCodeRepo;

	public ModelDao(DaoFactory daoFactory, ObjectProperty<TCConfig> config,
			Function<IModelDao, StorageDataHandlerRegistry> serDeserializerRegistry) {
		this.daoFactory = daoFactory;
		this.config = config;
		this.serDeserializerRegistry = serDeserializerRegistry.apply(this);
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
		this.testCaseRepo = daoFactory.loaderOf(TestCase.class, modelConfig.getTcPath(),
				serDeserializerRegistry);
		this.templateRepo = daoFactory.loaderOf(String.class, modelConfig.getTemplatePath(), templateRegistry);
		this.testCaseCodeRepo = daoFactory.loaderOf(String.class, modelConfig.getTcExportPath(),
				testCaseCodeRegistry);
	}

	@Override
	public List<Metadata> listDictionaries() {
		return uncheck("Listing of dictionaries",
				() -> dictionaryRepo.list()
						.map(f ->
								readTestDictionary(f.getLocator())
										.orElseThrow(() -> new IllegalStateException("Listed Dictionary not found"))
										.getMetadata()).toList());
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
			Logs.of(ModelDao.class).log(Level.WARNING, e, () -> "Unable to load dictionary " + dictionaryId);
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
						.map(f ->
								readTestCase(f.getLocator(), _ -> null).orElseThrow(() -> new IllegalStateException("Listed TestCase not found")))
						.filter(tc -> tc.getPreferredDictionary().equals(dictionary.getClassifier()))
						.map(TestCase::getMetadata)
						.toList());
	}

	@Override
	public Optional<TestCase> readTestCase(String locator, Function<String, TestDictionary> testDictionaryLoader) {
		try {
			final var testCase = testCaseRepo.load(locator);
			testCase.getMetadata().setTransientId(locator);
			testCase.setDictionary(testDictionaryLoader.apply(testCase.getPreferredDictionary()));
			return Optional.of(testCase);
		} catch (StorageException e) {
			Logs.of(ModelDao.class).log(Level.WARNING, e, () -> "Unable to load test case " + locator);
			return Optional.empty();
		}
	}

	@Override
	public void writeTestCase(String locator, TestCase tc) {
		tc.setPreferredDictionary(tc.getDictionary().getClassifier());
		uncheck("Writing of test case", () -> testCaseRepo.saveOrUpdate(locator, tc));
	}

	@Override
	public Template readTemplate(String templateLocator) {
		return uncheck("Reading of template", () -> new Template(templateRepo.load(templateLocator)));
	}

	@Override
	public Resource<String> writeTestCaseCode(String locator, String code) {
		return uncheck("Writing of test case code", () -> testCaseCodeRepo.saveOrUpdate(locator, code));
	}

	public ModelConfig getCurrentConfig() {
		return configOf(config.getValue());
	}

}
