package ch.skymarshall.dataflowmgr.generator;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;
import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.model.Transformer;
import ch.skymarshall.dataflowmgr.generator.readers.json.ActionJson;
import ch.skymarshall.dataflowmgr.generator.writers.Config;

public class JsonAdapter {

	private final ObjectMapper mapper = new ObjectMapper();

	public JsonAdapter() {
		final SimpleModule module = new SimpleModule();
		module.addDeserializer(ActionPoint.class, new ActionJson.ActionDeserializer());
		mapper.registerModule(module);
	}

	public Module readApplication(final InputStream in) throws JsonParseException, JsonMappingException, IOException {
		if (in == null) {
			throw new IllegalArgumentException("in == null");
		}
		return mapper.readValue(in, Module.class);
	}

	public Transformer readTransformer(final String templateName, final InputStream in)
			throws JsonParseException, JsonMappingException, IOException {
		if (in == null) {
			throw new IllegalArgumentException("in == null");
		}
		final Transformer readValue = mapper.readValue(in, Transformer.class);
		readValue.name = templateName;
		return readValue;
	}

	public Config readConfig(final InputStream in) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(in, Config.class);
	}

}
