package ch.scaille.tcwriter.model.persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.config.FsConfigManager;
import ch.scaille.tcwriter.config.IResourceLoader;
import ch.scaille.tcwriter.model.ExportReference;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.util.exceptions.StorageRTException;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.ExcExt;
import ch.scaille.util.helpers.LambdaExt;
import ch.scaille.util.helpers.Logs;

public class FsModelDao implements IModelDao {

	private static final String CONTEXT_ALL_REFERENCES = "AllTestReferences";
	private static final ObjectMapper mapper;

	static {
		ClassLoaderHelper.registerResourceHandler();
		mapper = JsonMapper.builder().configure(MapperFeature.AUTO_DETECT_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_FIELDS, true).visibility(PropertyAccessor.FIELD, Visibility.ANY)
				.activateDefaultTyping(new LaissezFaireSubTypeValidator(), DefaultTyping.NON_FINAL, As.WRAPPER_OBJECT)
				.build();

		final var testCaseWriterModule = new SimpleModule("TestCaseWriter");
		testCaseWriterModule.addDeserializer(ExportReference.class, new JsonDeserializer<>() {
			@Override
			public ExportReference deserialize(final JsonParser p, final DeserializationContext ctxt)
					throws IOException {
				if (!ExportReference.class.getName().equals(p.getCurrentName())) {
					throw new StorageRTException("Unexpected type in ExportReference: " + p.getCurrentName());
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
		mapper.registerModules(new GuavaModule(), testCaseWriterModule);
	}

	private final FsConfigManager configLoader;

	private IResourceLoader dictionaryResource;

	private IResourceLoader testCaseResource;

	private IResourceLoader templateResource;

	private IResourceLoader testCaseCodeResource;

	public FsModelDao(FsConfigManager configLoader) {
		this.configLoader = configLoader;
		this.configLoader.onReload(c -> reload(c.getSubconfig(FsModelConfig.class)));
	}

	private void reload(Optional<FsModelConfig> config) {
		var cfg = config.orElseThrow(() -> new IllegalStateException("Cannot find dao config"));
		this.dictionaryResource = configLoader.configure(cfg.getDictionaryPath(), "json");
		this.testCaseResource = configLoader.configure(cfg.getTcPath(), "json");
		this.templateResource = configLoader.configure(cfg.getTemplatePath(), null);
		this.testCaseCodeResource = configLoader.configure(cfg.getTcExportPath(), null);
	}

	@Override
	public List<Metadata> listDictionaries() throws IOException {
		return dictionaryResource.list().map(f -> readTestDictionary(f).get().getMetadata()).toList();
	}

	@Override
	public void writeTestDictionary(TestDictionary tm) {
		var id = tm.getMetadata().getTransientId();
		if (id.isEmpty()) {
			id = "default";
		}
		final var idSafe = id;
		ExcExt.uncheck(
				() -> dictionaryResource.write(idSafe, mapper.writerFor(TestDictionary.class).writeValueAsString(tm)));
	}

	@Override
	public Optional<TestDictionary> readTestDictionary(String dictionaryId) {
		try {
			final var dictionary = (TestDictionary) mapper.readerFor(TestDictionary.class)
					.readValue(dictionaryResource.read(dictionaryId));
			dictionary.getMetadata().setTransientId(dictionaryId);
			return Optional.of(dictionary);
		} catch (IOException e) {
			Logs.of(FsModelDao.class).log(Level.INFO, () -> "Unable to load dictionary " + e.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public void writeTestDictionary(Path target, TestDictionary tm) {
		ExcExt.uncheck(
				() -> dictionaryResource.write(target, mapper.writerFor(TestDictionary.class).writeValueAsString(tm)));
	}

	@Override
	public List<Metadata> listTestCases(final TestDictionary dictionary) throws IOException {
		return testCaseResource.list().map(f -> readTestCase(f, dictionary).get().getMetadata()).toList();
	}

	@Override
	public Optional<ExportableTestCase> readTestCase(String locator, TestDictionary testDictionary) {
		try {
			String tcJson;
			var tcpath = Paths.get(locator);
			if (tcpath.getNameCount() == 1) {
				tcJson = testCaseResource.read(tcpath.getFileName().toString());
			} else {
				tcJson = testCaseResource.read(tcpath);
			}

			var references = new ArrayList<ExportReference>();
			var ctxt = mapper.getDeserializationConfig().getAttributes().withPerCallAttribute(CONTEXT_ALL_REFERENCES,
					references);
			var testCase = (ExportableTestCase) mapper.readerFor(ExportableTestCase.class).with(ctxt).readValue(tcJson);
			testCase.setDictionary(testDictionary);
			testCase.getMetadata().setTransientId(locator);
			references.forEach(e -> e.restore(testCase));
			return Optional.of(testCase);
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	@Override
	public void writeTestCase(String locator, TestCase tc) {
		String tcJson = ExcExt.uncheck(() -> mapper.writerFor(TestCase.class).writeValueAsString(tc));
		var tcpath = Paths.get(locator);
		if (tcpath.getNameCount() == 1) {
			ExcExt.uncheck(() -> testCaseResource.write(tcpath.getFileName().toString(), tcJson));
		} else {
			ExcExt.uncheck(() -> testCaseResource.write(tcpath, tcJson));
		}
	}

	@Override
	public Template readTemplate() {
		return LambdaExt.uncheck(() -> new Template(templateResource.read("")));
	}

	@Override
	public String writeTestCaseCode(String locator, String code) {
		return ExcExt.uncheck(() -> testCaseCodeResource.write(locator, code));
	}

	public static String toIdentifier(Path path) {
		return path.getFileName().toString().replace(".json", "");
	}

	public Path getTCFolder() {
		return testCaseResource.getBaseFolder();
	}

}
