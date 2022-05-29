package ch.scaille.tcwriter.model.persistence;

import static ch.scaille.util.helpers.LambdaExt.uncheckF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
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
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import ch.scaille.tcwriter.generators.TCConfig;
import ch.scaille.tcwriter.model.ExportReference;
import ch.scaille.tcwriter.model.testapi.Metadata;
import ch.scaille.tcwriter.model.testapi.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.util.exceptions.StorageRTException;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.Logs;

public class FsModelDao implements IModelDao {
	private static final Logger LOGGER = Logs.of(FsModelDao.class);

	private static final String CONTEXT_ALL_REFERENCES = "AllTestReferences";
	private static ObjectMapper mapper;

	static {
		ClassLoaderHelper.registerResourceHandler();
		mapper = JsonMapper.builder().configure(MapperFeature.AUTO_DETECT_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_FIELDS, true).visibility(PropertyAccessor.FIELD, Visibility.ANY)
				.activateDefaultTyping(new LaissezFaireSubTypeValidator(), DefaultTyping.NON_FINAL, As.WRAPPER_OBJECT)
				.build();

		final var testCaseWriterModule = new SimpleModule("TestCaseWriter");
		testCaseWriterModule.addDeserializer(ExportReference.class, new JsonDeserializer<ExportReference>() {
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

	public static IModelDao withDefaultConfig() {
		try {
			return new FsModelDao(new TCConfig()).loadConfiguration("defaultConfig");
		} catch (IOException e) {
			LOGGER.log(Level.INFO, e, () -> "Unable to read default configuration");
			return new FsModelDao(new TCConfig());
		}
	}

	private TCConfig config;

	public FsModelDao(String configIdentifier) throws IOException {
		loadConfiguration(configIdentifier);
	}

	public FsModelDao(TCConfig config) {
		this.config = config;
	}

	@Override
	public Object getConfiguration() {
		return this.config;
	}

	@Override
	public IModelDao loadConfiguration(String identifier) throws IOException {
		this.config = (TCConfig) mapper.readerFor(TCConfig.class)
				.without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(read(configPath(identifier)));
		return this;
	}

	@Override
	public void saveConfiguration() throws IOException {
		writeJson(configPath(this.config.getName()), mapper.writeValueAsString(this.config));
	}

	@Override
	public List<Metadata> listDictionaries() throws IOException {
		try (Stream<Path> list = Files.list(Paths.get(resolveToURL(this.config.getDictionaryPath()).toURI()))) {
			return list.map(f -> f.getFileName().toString())
					.map(uncheckF(f -> readTestDictionary(f).getMetadata(), (f, e) -> {
						Logs.of(getClass()).log(Level.INFO, "Unable to read meta data", e);
						return new Metadata(f, "Unreadable: " + e.getClass());
					})).collect(Collectors.toList());
		} catch (IOException | URISyntaxException e) {
			throw new IOException("Unable to read dictionaries", e);
		}
	}

	@Override
	public TestDictionary readTestDictionary() throws IOException {
		return (TestDictionary) mapper.readerFor(TestDictionary.class)
				.readValue(read(resolveToURL(this.config.getDictionaryPath())));
	}

	@Override
	public void writeTestDictionary(TestDictionary tm) throws IOException {
		writeJson(resolveToURL(this.config.getDictionaryPath()),
				mapper.writerFor(TestDictionary.class).writeValueAsString(tm));
	}

	@Override
	public TestDictionary readTestDictionary(String dictionaryId) throws IOException {
		TestDictionary dictionary = (TestDictionary) mapper.readerFor(TestDictionary.class)
				.readValue(read(resolveToURL(this.config.getDictionaryPath() + '/' + dictionaryId)));
		dictionary.getMetadata().setTransientId(dictionaryId);
		return dictionary;
	}

	@Override
	public void writeTestDictionary(Path target, TestDictionary tm) throws IOException {
		writeJson(target.toUri().toURL(), mapper.writerFor(TestDictionary.class).writeValueAsString(tm));
	}

	@Override
	public TestCase readTestCase(String identifier, TestDictionary testDictionary) throws IOException {
		List<ExportReference> references = new ArrayList<>();
		var ctxt = mapper.getDeserializationConfig().getAttributes().withPerCallAttribute(CONTEXT_ALL_REFERENCES,
				references);
		var testCase = (TestCase) mapper.readerFor(TestCase.class).with(ctxt)
				.readValue(read(resolveJsonToUrl(this.config.getTcPath(), identifier)));
		testCase.setDictionary(testDictionary);
		references.forEach(e -> e.restore(testCase));
		return testCase;
	}

	@Override
	public void writeTestCase(String identifier, TestCase tc) throws IOException {
		writeJson(resolveJsonToUrl(this.config.getTcPath(), identifier),
				mapper.writerFor(TestCase.class).writeValueAsString(tc));
	}

	@Override
	public Template readTemplate() throws IOException {
		return new Template(read(resolveToURL(this.config.getTemplatePath())));
	}

	@Override
	public URI exportTestCase(String name, String content) throws IOException {
		var exportPath = Paths.get(resolve(this.config.getTCExportPath())).resolve(name);
		Logs.of(this).info(() -> "Writing " + exportPath);
		Files.createDirectories(exportPath.getParent());
		Files.write(exportPath, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
		return exportPath.toUri();
	}

	protected URL configPath(String identifier) throws MalformedURLException {
		return resolveJsonToUrl("${user.home}/.tcwriter", identifier);
	}

	protected void writeJson(URL path, String content) throws IOException {
		if (!path.getProtocol().equals("file")) {
			throw new IllegalStateException("Not possible to write url " + path);
		}
		try {
			var file = Paths.get(path.toURI());
			Files.createDirectories(file.getParent());
			Files.write(file, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (URISyntaxException e) {
			throw new IOException("Cannot write file", e);
		}
	}

	protected URL resolveToURL(String path) throws MalformedURLException {
		var saneUrl = resolve(path);
		try {
			return new URL(saneUrl);
		} catch (MalformedURLException e) {
			return Paths.get(saneUrl).toAbsolutePath().toUri().toURL();
		}
	}

	private String resolve(String path) {
		return path.replace("${user.home}", System.getProperty("user.home"));
	}

	protected URL resolveJsonToUrl(String path, String subPath) throws MalformedURLException {
		return resolveToURL(path + "/" + subPath + ".json");
	}

	protected String read(URL path) throws IOException {
		try (var reader = new BufferedReader(new InputStreamReader(path.openConnection().getInputStream()))) {
			return reader.lines().collect(Collectors.joining("\n"));
		}
	}

	public static Path classFile(Path root, String testClassName) {
		return root.resolve(testClassName.replace('.', '/') + ".java");
	}
}
