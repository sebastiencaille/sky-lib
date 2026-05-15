package ch.scaille.tcwriter.persistence;

import static ch.scaille.util.persistence.StorageRTException.uncheck;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

import ch.scaille.generators.util.Template;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.persistence.handlers.AbstractModelDataHandler;
import ch.scaille.tcwriter.persistence.handlers.JsonModelDataHandler;
import ch.scaille.tcwriter.persistence.handlers.TemplateStorageHandler;
import ch.scaille.tcwriter.persistence.handlers.YamlModelDataHandler;
import ch.scaille.tcwriter.persistence.handlers.YamlModelMetadataHandler;
import ch.scaille.util.persistence.DaoFactory;
import ch.scaille.util.persistence.DummyDao;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.Resource;
import ch.scaille.util.persistence.StorageException;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import ch.scaille.util.persistence.handlers.TextStorageHandler;
import lombok.extern.java.Log;
import org.jspecify.annotations.Nullable;

@Log
public class ModelDao implements IModelDao {

    public static StorageDataHandlerRegistry defaultDataHandlers() {
        final var modelSerDeserializerRegistry = new StorageDataHandlerRegistry(new YamlModelDataHandler());
        modelSerDeserializerRegistry.register(new JsonModelDataHandler());
        modelSerDeserializerRegistry.register(new TextStorageHandler());
        modelSerDeserializerRegistry.register(new TemplateStorageHandler());
        modelSerDeserializerRegistry.register(new YamlModelMetadataHandler());
        return modelSerDeserializerRegistry;
    }

    private static ModelConfig configOf(TCConfig config) {
        return config.getSubconfig(ModelConfig.class)
                .orElseThrow(() -> new IllegalStateException("Cannot find FsModelConfig"));
    }

    private final ObjectProperty<@Nullable TCConfig> config;

    private final StorageDataHandlerRegistry serDeserializerRegistry;

    private final StorageDataHandlerRegistry templateRegistry = new StorageDataHandlerRegistry(new TemplateStorageHandler());

    private final StorageDataHandlerRegistry testCaseCodeRegistry = new StorageDataHandlerRegistry(new TextStorageHandler());

    private final DaoFactory daoFactory;

    protected final MetadataCacheDao dictionaryMetadataCache;
    protected final MetadataCacheDao testcaseMetadataCache;

    protected IDao<TestDictionary> dictionaryRepo = new DummyDao<>();

    protected IDao<Metadata> dictionaryMetadataRepo = new DummyDao<>();
    
    protected IDao<TestCase> testCaseRepo = new DummyDao<>();
    
    protected IDao<Metadata> testCaseMetadataRepo = new DummyDao<>();

    protected IDao<String> templateRepo = new DummyDao<>();

    protected IDao<String> testCaseCodeRepo = new DummyDao<>();

    public ModelDao(DaoFactory daoFactory, ObjectProperty<@Nullable TCConfig> config,
                    DaoFactory.IDataSourceFactory cacheDsFactory,
                    StorageDataHandlerRegistry serDeserializerRegistry) {
        this.daoFactory = daoFactory;
        this.config = config;
        this.serDeserializerRegistry = serDeserializerRegistry;
        this.config.listen(this::reload);
        this.dictionaryMetadataCache = new MetadataCacheDao("Dictionary", cacheDsFactory);
        this.testcaseMetadataCache = new MetadataCacheDao("TestCase", cacheDsFactory);
        reload(this.config.getValue());
    }

    protected void reload(@Nullable TCConfig config) {
        if (config == null) {
            return;    
        }
        final var modelConfig = configOf(config);
        this.dictionaryRepo = daoFactory.loaderOf(TestDictionary.class, modelConfig.getDictionaryPath(),
                serDeserializerRegistry);
        this.dictionaryMetadataRepo = daoFactory.loaderOf(Metadata.class, modelConfig.getDictionaryPath(),
                serDeserializerRegistry);
        this.testCaseRepo = daoFactory.loaderOf(TestCase.class, modelConfig.getTcPath(),
                serDeserializerRegistry);
        this.testCaseMetadataRepo = daoFactory.loaderOf(Metadata.class, modelConfig.getTcPath(),
                serDeserializerRegistry);
        this.templateRepo = daoFactory.loaderOf(String.class, modelConfig.getTemplatePath(), templateRegistry);
        this.testCaseCodeRepo = daoFactory.loaderOf(String.class, modelConfig.getTcExportPath(),
                testCaseCodeRegistry);
    }
    
    @Override
    @Nullable
    public Metadata loadDictionaryMetadata(String locator) {
        return dictionaryMetadataCache.loadMetadata(locator,
                l -> {
                	try {
                		final var metadataResource = dictionaryMetadataRepo.resolve(l, AbstractModelDataHandler.METADATA_MIME_TYPE);
						final var metadata = dictionaryMetadataRepo.loadResource(metadataResource).getValue();
						metadata.setTransientId(locator);
						return metadata;
                	} catch (StorageException _) {
                		return null;
                	}
                });
    }

    @Override
    public List<Metadata> listDictionaries(@Nullable Metadata filter) {
        return uncheck("Unable to load dictionary", () -> dictionaryRepo.list()
                .map(f -> loadDictionaryMetadata(f.getLocator()))
                .filter(metadata -> metadata != null && (filter == null ||
                        filter.matches(metadata)))
                .toList());
    }
    
    @Override
    public void writeTestDictionary(TestDictionary dictionary) {
        var id = dictionary.getMetadata().getTransientId();
        if (id == null || id.isEmpty()) {
            id = "default";
        }
        final var idSafe = id;
        uncheck("Writing of test dictionary", () -> dictionaryRepo.saveOrUpdate(idSafe, dictionary));
        uncheck("Updating metadata", () -> testcaseMetadataCache.putInCache(idSafe, dictionary.getMetadata()));
    }   

    @Override
    public Optional<TestDictionary> readTestDictionary(String dictionaryId) {
        try {
            final var dictionary = dictionaryRepo.load(dictionaryId);
            dictionary.getMetadata().setTransientId(dictionaryId);
            return Optional.of(dictionary);
        } catch (StorageException e) {
            log.log(Level.WARNING, e, () -> "Unable to load dictionary " + dictionaryId);
            return Optional.empty();
        }
    }

    @Override
    @Nullable
    public Metadata loadTestCaseMetadata(String locator) {
        return testcaseMetadataCache.loadMetadata(locator,
                l -> {
                	try {
                		final var metadataResource = testCaseMetadataRepo.resolve(l, AbstractModelDataHandler.METADATA_MIME_TYPE);
						final var metadata = testCaseMetadataRepo.loadResource(metadataResource).getValue();
						metadata.setTransientId(locator);
						return metadata;
                	} catch (StorageException _) {
                		return null;
                	}
                });
    }

    @Override
    public List<Metadata> listTestCases(@Nullable final Metadata dictionary) {
        return uncheck("Unable to load test case list", () -> testCaseRepo.list()
                .map(f -> loadTestCaseMetadata(f.getLocator()))
                .filter(tcMetadata -> tcMetadata != null && (dictionary == null ||
                        tcMetadata.matches(dictionary)))
                .toList());
    }

    @Override
    public Optional<TestCase> readTestCase(String locator, TestDictionary dictionary) {
        try {
            final var testCase = testCaseRepo.load(locator, new TestCase("", dictionary));
            testCase.getMetadata().setTransientId(locator);
            return Optional.of(testCase);
        } catch (StorageException e) {
            log.log(Level.WARNING, e, () -> "Unable to load test case " + locator);
            return Optional.empty();
        }
    }

    @Override
    public void writeTestCase(String locator, TestCase tc) {
        uncheck("Writing of test case", () -> testCaseRepo.saveOrUpdate(locator, tc));
        uncheck("Updating metadata", () -> testcaseMetadataCache.putInCache(locator, tc.getMetadata()));
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
        return configOf(Objects.requireNonNull(config.getValue(), "Config was not loaded"));
    }

}
