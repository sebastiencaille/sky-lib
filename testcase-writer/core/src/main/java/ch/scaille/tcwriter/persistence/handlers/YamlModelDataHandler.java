package ch.scaille.tcwriter.persistence.handlers;

import tools.jackson.dataformat.yaml.YAMLMapper;

public class YamlModelDataHandler extends AbstractJacksonDataHandler {

	public YamlModelDataHandler() {
		super(configure(YAMLMapper.builder()
				.addModule(testCaseWriterModule))
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
		return YAML_MIME_TYPE.equals(extensionOrType) || (YAML_EXT_1.equals(extensionOrType) || YAML_EXT_2.equals(extensionOrType));
	}


}
