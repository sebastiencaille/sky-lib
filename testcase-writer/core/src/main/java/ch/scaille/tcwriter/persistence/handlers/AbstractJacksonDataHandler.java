package ch.scaille.tcwriter.persistence.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.cfg.ConstructorDetector;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.node.StringNode;
import tools.jackson.datatype.guava.GuavaModule;

import ch.scaille.tcwriter.model.ExportReference;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.util.persistence.StorageRTException;
import ch.scaille.util.persistence.handlers.IStorageDataHandler;

public abstract class AbstractJacksonDataHandler implements IStorageDataHandler {

	protected static final String YAML_EXT_1 = "yaml";
	protected static final String YAML_EXT_2 = "yml";
	public static final String YAML_MIME_TYPE = "application/yaml";

	protected static final String JSON_EXT_1 = "json";
	public static final String JSON_MIME_TYPE = "application/json";

	private static final String CONTEXT_ALL_REFERENCES = "AllTestReferences";

	protected static final SimpleModule testCaseWriterModule = new SimpleModule("TCWriterModel");
	static {
		testCaseWriterModule.addDeserializer(ExportReference.class, new ValueDeserializer<>() {
			@Override
			public ExportReference deserialize(final JsonParser p, final DeserializationContext ctxt) {
				// yaml and json have different behavior
				if (!ExportReference.class.getName().equals(p.getTypeId())
						&& !ExportReference.class.getName().equals(p.currentName())) {
					throw new StorageRTException("Unexpected type in ExportReference: " + p.getTypeId());
				}
				final var content = p.readValueAsTree();
				final var id = content.get("id");
				if (id == null) {
					throw new StorageRTException("Unexpected attribute in ExportReference: " + p.currentName());
				}
				final var exportReference = new ExportReference(((StringNode) id).asString());
				((List<ExportReference>) ctxt.getAttribute(CONTEXT_ALL_REFERENCES)).add(exportReference);
				return exportReference;
			}
		});
	}

	protected final ObjectMapper mapper;

	protected AbstractJacksonDataHandler(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	protected static <M extends ObjectMapper, B extends MapperBuilder<M, B>> MapperBuilder<M, B> configure(
			MapperBuilder<M, B> builder) {
		return builder.configure(MapperFeature.USE_ANNOTATIONS, true)
				.changeDefaultVisibility(checker -> checker.withFieldVisibility(Visibility.ANY))
				.constructorDetector(ConstructorDetector.EXPLICIT_ONLY)
				.activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
						.allowIfBaseType("ch.scaille.tcwriter.")
						.allowIfBaseType("java.util.")
						.allowIfBaseType("com.google.common.collect.")
						.allowIfSubType("ch.scaille.tcwriter.")
						.build(), DefaultTyping.NON_FINAL)
				.addModule(new GuavaModule());
	}

	@Override
	public <T> String encode(Class<T> targetClass, T value) throws IOException {
		return mapper.writerFor(targetClass).writeValueAsString(value);
	}
	
	@Override
	public <T> T decode(String value, Class<T> targetType) throws IOException {
		if (ExportableTestCase.class.isAssignableFrom(targetType)) {
			final var references = new ArrayList<ExportReference>();
			final var tc = (ExportableTestCase) mapper.readerFor(ExportableTestCase.class)
					.with(mapper.deserializationConfig().getAttributes().withPerCallAttribute(CONTEXT_ALL_REFERENCES,
							references))
					.readValue(value);
			tc.setExportedReferences(references);
			return (T) tc;
		}
        final var deserializeObject = mapper.readerFor(targetType).readValue(value);
        if (deserializeObject instanceof TestDictionary testDictionary) {
            testDictionary.getActors().values().forEach(actor ->  actor.restore(testDictionary.getRoles()));
        }
        return (T)deserializeObject;
	}

}
