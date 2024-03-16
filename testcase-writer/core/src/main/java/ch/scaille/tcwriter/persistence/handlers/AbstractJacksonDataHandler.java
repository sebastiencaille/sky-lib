package ch.scaille.tcwriter.persistence.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

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
		testCaseWriterModule.addDeserializer(ExportReference.class, new JsonDeserializer<>() {
			@Override
			public ExportReference deserialize(final JsonParser p, final DeserializationContext ctxt)
					throws IOException {
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
				final var exportReference = new ExportReference(((TextNode) id).asText());
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
		return builder.configure(MapperFeature.AUTO_DETECT_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_FIELDS, true).visibility(PropertyAccessor.FIELD, Visibility.ANY)
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
					.with(mapper.getDeserializationConfig().getAttributes().withPerCallAttribute(CONTEXT_ALL_REFERENCES,
							references))
					.readValue(value);
			tc.setExportedReferences(references);
			return (T) tc;
		}
		return mapper.readerFor(targetType).readValue(value);
	}

}
