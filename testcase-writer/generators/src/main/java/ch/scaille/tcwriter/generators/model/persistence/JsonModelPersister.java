package ch.scaille.tcwriter.generators.model.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
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

import ch.scaille.tcwriter.generators.TCConfig;
import ch.scaille.tcwriter.generators.model.ExportReference;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;
import ch.scaille.util.generators.Template;
import ch.scaille.util.helpers.ClassLoaderHelper;

public class JsonModelPersister implements IModelPersister {

	private static final Logger LOGGER = Logger.getLogger(JsonModelPersister.class.getName());

	private static final String CONTEXT_ALL_REFERENCES = "AllTestReferences";
	private static ObjectMapper mapper;

	static {
		ClassLoaderHelper.registerResourceHandler();
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

	private TCConfig config;

	public JsonModelPersister() {
		this("defaultConfig");
	}

	public JsonModelPersister(final String configIdentifier) {
		try {
			this.config = readConfiguration(configIdentifier);
		} catch (IOException e) {
			LOGGER.log(Level.INFO, e, () -> "Unable to read config " + configIdentifier);
			this.config = new TCConfig();
		}
	}

	public JsonModelPersister(final TCConfig config) {
		this.config = config;
	}

	@Override
	public TCConfig getConfiguration() {
		return config;
	}

	@Override
	public void setConfiguration(final TCConfig config) {
		this.config = config;
	}

	@Override
	public TCConfig readConfiguration(final String identifier) throws IOException {
		return mapper.readerFor(TCConfig.class).without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.readValue(read(configPath(identifier)));
	}

	@Override
	public void writeConfiguration(final TCConfig config) throws IOException {
		writeJson(configPath(config.getName()), mapper.writeValueAsString(config));
	}

	@Override
	public TestDictionary readTestDictionary() throws IOException {
		return mapper.readerFor(TestDictionary.class).readValue(read(resolveToURL(config.getDictionaryPath())));
	}

	@Override
	public void writeTestDictionary(final TestDictionary tm) throws IOException {
		writeJson(resolveToURL(config.getDictionaryPath()),
				mapper.writerFor(TestDictionary.class).writeValueAsString(tm));
	}

	@Override
	public void writeTestDictionary(final Path target, final TestDictionary tm) throws IOException {
		writeJson(target.toUri().toURL(), mapper.writerFor(TestDictionary.class).writeValueAsString(tm));
	}

	@Override
	public TestCase readTestCase(final String identifier, final TestDictionary testDictionary) throws IOException {
		final List<ExportReference> references = new ArrayList<>();
		final ContextAttributes ctxt = mapper.getDeserializationConfig().getAttributes()
				.withPerCallAttribute(CONTEXT_ALL_REFERENCES, references);
		final TestCase testCase = mapper.readerFor(TestCase.class).with(ctxt)
				.readValue(read(resolveJsonToUrl(config.getTcPath(), identifier)));
		testCase.setDictionary(testDictionary);
		references.forEach(e -> e.restore(testCase));
		return testCase;
	}

	@Override
	public void writeTestCase(final String identifier, final TestCase tc) throws IOException {
		writeJson(resolveJsonToUrl(config.getTcPath(), identifier),
				mapper.writerFor(TestCase.class).writeValueAsString(tc));
	}

	@Override
	public Template readTemplate() throws IOException {
		return new Template(read(resolveToURL(config.getTemplatePath())));
	}

	protected URL configPath(final String identifier) throws MalformedURLException {
		return resolveJsonToUrl("${user.home}/.tcwriter", identifier);
	}

	protected void writeJson(final URL path, final String content) throws IOException {
		if (!path.getProtocol().equals("file")) {
			throw new IllegalStateException("Not possible to write url " + path);
		}
		try {
			Path file = Paths.get(path.toURI());
			Files.createDirectories(file.getParent());
			Files.write(file, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (URISyntaxException e) {
			throw new IOException("Cannot write file", e);
		}
	}

	protected URL resolveToURL(String path) throws MalformedURLException {
		String saneUrl = resolve(path);
		try {
			return new URL(saneUrl);
		} catch (MalformedURLException e) {
			return Paths.get(saneUrl).toAbsolutePath().toUri().toURL();
		}
	}

	private String resolve(String path) {
		String saneUrl = path.replace("${user.home}", System.getProperty("user.home"));
		return saneUrl;
	}

	protected URL resolveJsonToUrl(String path, String subPath) throws MalformedURLException {
		return resolveToURL(path + '/' + subPath + ".json");
	}

	protected String read(final URL path) throws IOException {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(path.openConnection().getInputStream()))) {
			return reader.lines().collect(Collectors.joining("\n"));
		}
	}

	@Override
	public Path getExportedTCPath() {
		return Paths.get(resolve(config.getTCExportPath()));
	}

	public static Path classFile(final Path root, final String testClassName) {
		return root.resolve(testClassName.replace('.', '/') + ".java");
	}

}
