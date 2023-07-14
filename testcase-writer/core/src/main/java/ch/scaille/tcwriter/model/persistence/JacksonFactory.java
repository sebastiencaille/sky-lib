package ch.scaille.tcwriter.model.persistence;

import java.util.function.BiFunction;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

public class JacksonFactory {

	private final ObjectMapper jsonMapper;

	private final YAMLMapper yamlMapper;

	public JacksonFactory() {
		this(new GuavaModule());
	}

	public JacksonFactory(com.fasterxml.jackson.databind.Module extraModule) {
		this(extraModule, new GuavaModule());
	}

	private <M extends ObjectMapper, B extends MapperBuilder<M, B>> MapperBuilder<M, B> configure(
			MapperBuilder<M, B> builder) {
		return builder.configure(MapperFeature.AUTO_DETECT_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_FIELDS, true).visibility(PropertyAccessor.FIELD, Visibility.ANY);
	}

	private JacksonFactory(com.fasterxml.jackson.databind.Module... modules) {
		jsonMapper = configure(JsonMapper.builder().activateDefaultTyping(new LaissezFaireSubTypeValidator(),
				DefaultTyping.NON_FINAL, As.WRAPPER_OBJECT)).build();
		jsonMapper.registerModules(modules);

		yamlMapper = configure(YAMLMapper.builder().configure(Feature.USE_NATIVE_TYPE_ID, true)
				.configure(Feature.USE_NATIVE_OBJECT_ID, true)).build();
		yamlMapper.registerModules(modules);
	}

	public ObjectMapper json() {
		return jsonMapper;
	}

	public ObjectMapper yaml() {
		return yamlMapper;
	}

	public ObjectMapper of(String mimeType) {
		if (Resource.MIMETYPE_JSON.equalsIgnoreCase(mimeType)) {
			return jsonMapper;
		}
		return yamlMapper;
	}

	public <T> Resource.Decoder<T> of(Class<T> clazz) {
		return r -> of(r.type()).readerFor(clazz).readValue(r.data());
	}

	public <T> Resource.Decoder<T> of(Class<T> clazz, BiFunction<ObjectMapper, ObjectReader, ObjectReader> tunings) {
		return r -> {
			var mapper = of(r.type());
			return tunings.apply(mapper, mapper.readerFor(clazz)).readValue(r.data());
		};
	}
}
