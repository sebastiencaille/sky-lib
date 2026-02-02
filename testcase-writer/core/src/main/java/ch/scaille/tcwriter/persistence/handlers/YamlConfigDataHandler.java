package ch.scaille.tcwriter.persistence.handlers;

import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

public class YamlConfigDataHandler extends AbstractModelDataHandler {

	public YamlConfigDataHandler() {
		super(null, configure(YAMLMapper.builder()
				.configure(YAMLWriteFeature.USE_NATIVE_OBJECT_ID, true))
				.build());
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
