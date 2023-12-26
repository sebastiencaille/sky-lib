package ch.scaille.tcwriter.persistence.handlers;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

public class JsonModelDataHandler extends AbstractJacksonDataHandler {

	public JsonModelDataHandler() {
		super(configure(JsonMapper.builder().activateDefaultTyping(new LaissezFaireSubTypeValidator(),
				DefaultTyping.NON_FINAL, As.PROPERTY)).addModule(testCaseWriterModule).build());
	}
	
	@Override
	public String getDefaultExtension() {
		return JSON_EXT_1;
	}

	@Override
	public String getDefaultMimeType() {
		return JSON_MIME_TYPE;
	}

	@Override
	public boolean supports(String extensionOrType) {
		return JSON_MIME_TYPE.equals(extensionOrType) || JSON_EXT_1.equals(extensionOrType);
	}

}
