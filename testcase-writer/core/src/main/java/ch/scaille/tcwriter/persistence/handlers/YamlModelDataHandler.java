package ch.scaille.tcwriter.persistence.handlers;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class YamlModelDataHandler extends AbstractJacksonDataHandler {

	public YamlModelDataHandler() {
		super(configure(YAMLMapper.builder().activateDefaultTyping(new LaissezFaireSubTypeValidator(),
				DefaultTyping.NON_FINAL, As.PROPERTY)).addModule(testCaseWriterModule).build());
	}

	@Override
	public String getDefaultExtension() {
		return YAML_EXT_1;
	}
	
	@Override
	public String getDefaultMimeType() {
		return YAML_MIME_TYPE;
	}

	@Override
	public boolean supports(String extensionOrType) {
		return YAML_MIME_TYPE.equals(extensionOrType) || (YAML_EXT_1.equals(extensionOrType) || YAML_EXT_2.equals(extensionOrType));
	}


}
