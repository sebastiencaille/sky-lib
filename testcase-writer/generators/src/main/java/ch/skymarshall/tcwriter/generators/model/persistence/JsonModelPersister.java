package ch.skymarshall.tcwriter.generators.model.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import ch.skymarshall.tcwriter.generators.GeneratorConfig;
import ch.skymarshall.tcwriter.generators.model.ExportReference;
import ch.skymarshall.tcwriter.generators.model.testapi.TestDictionary;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;

public class JsonModelPersister implements IModelPersister {

	private static final String CONTEXT_ALL_REFERENCES = "AllTestReferences";
	private static ObjectMapper mapper;

	static {
		mapper = JsonMapper.builder().configure(MapperFeature.AUTO_DETECT_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_FIELDS, true).visibility(PropertyAccessor.FIELD, Visibility.ANY)
				.activateDefaultTyping(new LaissezFaireSubTypeValidator(), DefaultTyping.NON_FINAL, As.WRAPPER_OBJECT)
				.build();

		final SimpleModule testCaseWriterModule = new SimpleModule("TestCaseWriter");
		testCaseWriterModule.addDeserializer(ExportReference.class, new JsonDeserializer<ExportReference>() {
			@Override
			public ExportReference deserialize(final JsonParser p, final DeserializationContext ctxt)
					throws IOException {
				if (!ExportReference.class.getName().equals(p.getCurrentName())) {
					throw new IllegalStateException("Unexpected type in ExportReference: " + p.getCurrentName());
				}
				final TreeNode content = p.readValueAsTree();
				final TreeNode id = content.get("id");
				if (id == null) {
					throw new IllegalStateException("Unexpected attribute in ExportReference: " + p.getCurrentName());
				}
				final ExportReference exportReference = new ExportReference(((TextNode) id).asText());
				((List<ExportReference>) ctxt.getAttribute(CONTEXT_ALL_REFERENCES)).add(exportReference);
				return exportReference;
			}
		});

		mapper.registerModules(new GuavaModule(), testCaseWriterModule);

	}

	private GeneratorConfig config = new GeneratorConfig();

	public JsonModelPersister() throws IOException {
		this("defaultConfig");
	}

	public JsonModelPersister(final String configIdentifier) throws IOException {
		this.config = readConfiguration(configIdentifier);
	}

	public JsonModelPersister(final GeneratorConfig config) {
		this.config = config;
	}

	@Override
	public void setConfiguration(final GeneratorConfig config) {
		this.config = config;
	}

	protected Path tcPath(final String identifier) {
		return Paths.get(config.getTcPath(), identifier);
	}

	private static Path configPath(final String identifier) {
		return Paths.get(System.getProperty("user.home"), ".tcwriter", identifier);
	}

	protected static String readJson(final Path path) throws IOException {
		return String.join(" ", Files.readAllLines(path, StandardCharsets.UTF_8));
	}

	protected static void writeJson(final Path path, final String content) throws IOException {
		Files.createDirectories(path.getParent());
		Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	@Override
	public TestDictionary readTestDictionary() throws IOException {
		return mapper.readerFor(TestDictionary.class).readValue(readJson(Paths.get(config.getDictionaryPath())));
	}

	@Override
	public void writeTestDictionary(final TestDictionary tm) throws IOException {
		writeTestDictionary(Paths.get(config.getDictionaryPath()), tm);
	}

	@Override
	public void writeTestDictionary(final Path target, final TestDictionary tm) throws IOException {
		writeJson(target, mapper.writerFor(TestDictionary.class).writeValueAsString(tm));
	}

	@Override
	public TestCase readTestCase(final String identifier, final TestDictionary testDictionary) throws IOException {
		final ArrayList<ExportReference> references = new ArrayList<>();
		final ContextAttributes ctxt = mapper.getDeserializationConfig().getAttributes()
				.withPerCallAttribute(CONTEXT_ALL_REFERENCES, references);
		final TestCase testCase = mapper.readerFor(TestCase.class).with(ctxt).readValue(readJson(tcPath(identifier)));
		testCase.setDictionary(testDictionary);
		references.forEach(e -> e.restore(testCase));
		return testCase;
	}

	@Override
	public void writeTestCase(final String identifier, final TestCase tc) throws IOException {
		writeJson(tcPath(identifier), mapper.writerFor(TestCase.class).writeValueAsString(tc));
	}

	@Override
	public GeneratorConfig readConfiguration(final String identifier) throws IOException {
		return mapper.readValue(readJson(configPath(identifier)), GeneratorConfig.class);
	}

	@Override
	public void writeConfiguration(final GeneratorConfig config) throws IOException {
		writeJson(configPath(config.getName() + ".json"), mapper.writeValueAsString(config));
	}

	public static Path classFile(final Path root, final String testClassName) {
		Path result = root;
		for (final String path : testClassName.split("\\.")) {
			result = result.resolve(path);
		}
		result.getParent().resolve(result.getFileName().toString() + ".java");
		return result;
	}

}
