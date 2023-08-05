package ch.scaille.tcwriter.persistence.handlers;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class YamlConfigDataHandler extends AbstractJacksonDataHandler {

	public YamlConfigDataHandler() {
		super(configure(YAMLMapper.builder().configure(Feature.USE_NATIVE_TYPE_ID, true)
				.configure(Feature.USE_NATIVE_OBJECT_ID, true)).build());
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
		return YAML_MIME_TYPE.equals(extensionOrType)
				|| (YAML_EXT_1.equals(extensionOrType) || YAML_EXT_2.equals(extensionOrType));
	}

}
