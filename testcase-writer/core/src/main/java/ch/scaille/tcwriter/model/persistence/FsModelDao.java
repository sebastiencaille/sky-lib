package ch.scaille.tcwriter.model.persistence;

import static ch.scaille.util.helpers.LambdaExt.uncheckF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
import ch.scaille.tcwriter.model.ExportReference;
import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.util.exceptions.StorageRTException;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.Logs;

public class FsModelDao implements IModelDao {
	private static final Logger LOGGER = Logs.of(FsModelDao.class);

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

	public static FsModelConfig loadConfiguration(String identifier) throws IOException {
		var toLoad = identifier;
		if (toLoad == null) {
			toLoad = "default";
		}
		return mapper.readerFor(FsModelConfig.class).without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.readValue(read(configPath(toLoad)));
	}

	public static FsModelConfig loadConfiguration() throws IOException {
		return loadConfiguration(null);
	}

	private FsModelConfig config;

	public FsModelDao(FsModelConfig config) {
		this.config = config;
	}

	@Override
	public Object getConfiguration() {
		return this.config;
	}

	@Override
	public void saveConfiguration() throws IOException {
		writeJson(configPath(this.config.getName()), mapper.writeValueAsString(this.config));
	}

	@Override
	public List<Metadata> listDictionaries() throws IOException {
		try (var list = Files.list(Paths.get(resolveToURL(this.config.getDictionaryPath()).toURI()))) {
			return list.map(FsModelDao::toIdentifier).map(uncheckF(f -> readTestDictionary(f).getMetadata(), //
					(f, e) -> {
						Logs.of(getClass()).log(Level.INFO, "Unable to read meta data", e);
						return new Metadata(f, "Unreadable: " + e.getClass());
					})).toList();
		} catch (IOException | URISyntaxException e) {
			throw new IOException("Unable to read dictionaries", e);
		}
	}

	@Override
	public void writeTestDictionary(TestDictionary tm) throws IOException {
		writeJson(resolveToURL(this.config.getDictionaryPath()),
				mapper.writerFor(TestDictionary.class).writeValueAsString(tm));
	}

	@Override
	public TestDictionary readTestDictionary(String dictionaryId) throws IOException {
		final var dictionary = (TestDictionary) mapper.readerFor(TestDictionary.class)
				.readValue(read(resolveJsonToUrl(this.config.getDictionaryPath(), dictionaryId)));
		dictionary.getMetadata().setTransientId(dictionaryId);
		return dictionary;
	}

	@Override
	public void writeTestDictionary(Path target, TestDictionary tm) throws IOException {
		writeJson(target.toUri().toURL(), mapper.writerFor(TestDictionary.class).writeValueAsString(tm));
	}

	@Override
	public List<Metadata> listTestCases(final TestDictionary dictionary) throws IOException {
		try (var list = Files.list(Paths.get(resolveToURL(this.config.getTcPath()).toURI()))) {
			return list.map(FsModelDao::toIdentifier).map(uncheckF(f -> readTestCase(f, dictionary).getMetadata(), //
					(f, e) -> {
						Logs.of(getClass()).log(Level.INFO, "Unable to read meta data", e);
						return new Metadata(f, "Unreadable: " + e.getClass());
					})).toList();
		} catch (IOException | URISyntaxException e) {
			throw new IOException("Unable to read dictionaries", e);
		}
	}

	@Override
	public ExportableTestCase readTestCase(String identifier, TestDictionary testDictionary) throws IOException {
		var references = new ArrayList<ExportReference>();
		var ctxt = mapper.getDeserializationConfig().getAttributes().withPerCallAttribute(CONTEXT_ALL_REFERENCES,
				references);
		var testCase = (ExportableTestCase) mapper.readerFor(ExportableTestCase.class).with(ctxt)
				.readValue(read(resolveJsonToUrl(this.config.getTcPath(), identifier)));
		testCase.setDictionary(testDictionary);
		testCase.getMetadata().setTransientId(identifier);
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
		Files.writeString(exportPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		return exportPath.toUri();
	}

	protected void writeJson(URL path, String content) throws IOException {
		LOGGER.info(() -> "Writing " + path);
		if (!path.getProtocol().equals("file")) {
			throw new IllegalStateException("Not possible to write url " + path);
		}
		try {
			var file = Paths.get(path.toURI());
			Files.createDirectories(file.getParent());
			Files.writeString(file, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (URISyntaxException e) {
			throw new IOException("Cannot write file", e);
		}
	}

	protected static URL configPath(String identifier) throws MalformedURLException {
		return resolveJsonToUrl("${user.home}/.tcwriter", identifier);
	}

	protected static URL resolveJsonToUrl(String path, String subPath) throws MalformedURLException {
		return resolveToURL(path + "/" + subPath + ".json");
	}

	protected static URL resolveToURL(String path) throws MalformedURLException {
		var saneUrl = resolve(path);
		try {
			return new URL(saneUrl);
		} catch (MalformedURLException e) {
			return Paths.get(saneUrl).toAbsolutePath().toUri().toURL();
		}
	}

	private static String resolve(String path) {
		return path.replace("${user.home}", System.getProperty("user.home")).replace("~",
				System.getProperty("user.home"));
	}

	protected static String read(URL path) throws IOException {
		LOGGER.info(() -> "Reading " + path);
		try (var reader = new BufferedReader(new InputStreamReader(path.openConnection().getInputStream()))) {
			return reader.lines().collect(Collectors.joining("\n"));
		}
	}

	public static Path classFile(Path root, String testClassName) {
		return root.resolve(testClassName.replace('.', '/') + ".java");
	}

	public static String toIdentifier(Path path) {
		return path.getFileName().toString().replace(".json", "");
	}
}
