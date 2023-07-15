package ch.scaille.tcwriter.model.persistence.fsmodel;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.config.TCConfig;
import ch.scaille.tcwriter.model.ExportReference;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.persistence.IConfigDao;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.tcwriter.model.persistence.IResourceRepository;
import ch.scaille.tcwriter.model.persistence.JacksonFactory;
import ch.scaille.tcwriter.model.persistence.Resource;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.util.exceptions.StorageRTException;
import ch.scaille.util.helpers.ExcExt;
import ch.scaille.util.helpers.LambdaExt;
import ch.scaille.util.helpers.Logs;

public class FsModelDao implements IModelDao {

	private static final JacksonFactory jacksonFactory;

	private static final String CONTEXT_ALL_REFERENCES = "AllTestReferences";

	static {
		final var testCaseWriterModule = new SimpleModule("TCWriterModel");
		testCaseWriterModule.addDeserializer(ExportReference.class, new JsonDeserializer<>() {
			@Override
			public ExportReference deserialize(final JsonParser p, final DeserializationContext ctxt)
					throws IOException {
				// yaml and json have different behavior
				if (!ExportReference.class.getName().equals(p.getTypeId())
						&& !ExportReference.class.getName().equals(p.getCurrentName())) {
					throw new StorageRTException("Unexpected type in ExportReference: " + p.getTypeId());
				}
				final var content = p.readValueAsTree();
				final var id = content.get("id");
				if (id == null) {
					throw new StorageRTException("Unexpected attribute in ExportReference: " + p.getCurrentName());
				}
				final var exportReference = new ExportReference(((TextNode) id).asText());
				((List<ExportReference>) ctxt.getAttribute(CONTEXT_ALL_REFERENCES)).add(exportReference);
				return exportReference;
			}
		});
		jacksonFactory = new JacksonFactory(testCaseWriterModule);
	}

	private final IConfigDao configDao;

	private IResourceRepository dictionaryRepo;

	private IResourceRepository testCaseRepo;

	private IResourceRepository templateRepo;

	private IResourceRepository testCaseCodeRepo;

	private static FsModelConfig configOf(TCConfig config) {
		return config.getSubconfig(FsModelConfig.class)
				.orElseThrow(() -> new IllegalStateException("Cannot find dao config"));
	}

	public FsModelDao(IConfigDao configLoader) {
		this.configDao = configLoader;
		this.configDao.onReload(c -> reload(configOf(c)));
	}

	private void reload(FsModelConfig config) {
		this.dictionaryRepo = configDao.loaderOf(config.getDictionaryPath(), "yaml");
		this.testCaseRepo = configDao.loaderOf(config.getTcPath(), "yaml");
		this.templateRepo = configDao.loaderOf(config.getTemplatePath(), null);
		this.testCaseCodeRepo = configDao.loaderOf(config.getTcExportPath(), null);
	}

	@Override
	public List<Metadata> listDictionaries() throws IOException {
		return dictionaryRepo.list().map(f -> readTestDictionary(f).get().getMetadata()).toList();
	}

	@Override
	public void writeTestDictionary(TestDictionary tm) {
		var id = tm.getMetadata().getTransientId();
		if (id.isEmpty()) {
			id = "default";
		}
		final var idSafe = id;
		ExcExt.uncheck(() -> dictionaryRepo.write(idSafe,
				jacksonFactory.yamlModel().writerFor(TestDictionary.class).writeValueAsString(tm)));
	}

	@Override
	public Optional<TestDictionary> readTestDictionary(String dictionaryId) {
		try {
			final var dictionary = dictionaryRepo.read(dictionaryId).decode(jacksonFactory.of(TestDictionary.class));
			dictionary.getMetadata().setTransientId(dictionaryId);
			return Optional.of(dictionary);
		} catch (IOException e) {
			Logs.of(FsModelDao.class).log(Level.WARNING, e, () -> "Unable to load dictionary");
			return Optional.empty();
		}
	}

	@Override
	public void writeTestDictionary(Path target, TestDictionary tm) {
		ExcExt.uncheck(() -> dictionaryRepo.write(target.toString(),
				jacksonFactory.yamlModel().writerFor(TestDictionary.class).writeValueAsString(tm)));
	}

	@Override
	public List<Metadata> listTestCases(final TestDictionary dictionary) throws IOException {
		return testCaseRepo.list().map(f -> readTestCase(f, dictionary).get().getMetadata()).toList();
	}

	@Override
	public Optional<ExportableTestCase> readTestCase(String locator, TestDictionary testDictionary) {
		try {
			final Resource tcRawData;
			final var tcpath = Paths.get(locator);
			if (tcpath.getNameCount() == 1) {
				tcRawData = testCaseRepo.read(tcpath.getFileName().toString());
			} else {
				tcRawData = testCaseRepo.read(tcpath.toString());
			}

			final var references = new ArrayList<ExportReference>();
			final var testCase = tcRawData
					.decode(jacksonFactory.of(ExportableTestCase.class, (m, r) -> r.with(m.getDeserializationConfig()
							.getAttributes().withPerCallAttribute(CONTEXT_ALL_REFERENCES, references))));
			testCase.setDictionary(testDictionary);
			testCase.getMetadata().setTransientId(locator);
			references.forEach(e -> e.restore(testCase));
			return Optional.of(testCase);
		} catch (IOException e) {
			Logs.of(FsModelDao.class).log(Level.WARNING, e, () -> "Unable to load test case " + locator);
			return Optional.empty();
		}
	}

	@Override
	public void writeTestCase(String locator, TestCase tc) {
		final var tcJson = ExcExt.uncheck(() -> jacksonFactory.yamlModel().writerFor(TestCase.class).writeValueAsString(tc));
		final var tcpath = Paths.get(locator);
		if (tcpath.getNameCount() == 1) {
			ExcExt.uncheck(() -> testCaseRepo.write(tcpath.getFileName().toString(), tcJson));
		} else {
			ExcExt.uncheck(() -> testCaseRepo.write(tcpath.toString(), tcJson));
		}
	}

	@Override
	public Template readTemplate() {
		return LambdaExt.uncheck(() -> new Template(templateRepo.read("").data()));
	}

	@Override
	public String writeTestCaseCode(String locator, String code) {
		return ExcExt.uncheck(() -> testCaseCodeRepo.write(locator, code));
	}

	public static String toIdentifier(Path path) {
		return path.getFileName().toString().replace(".json", "");
	}

	public Path getTCFolder() {
		return Path.of(configOf(configDao.getCurrentConfig()).getTcPath());
	}

}
