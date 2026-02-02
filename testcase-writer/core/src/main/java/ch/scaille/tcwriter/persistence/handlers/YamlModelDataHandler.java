package ch.scaille.tcwriter.persistence.handlers;

import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.persistence.handlers.serdeser.Deserializers;
import tools.jackson.dataformat.yaml.YAMLMapper;

public class YamlModelDataHandler extends AbstractModelDataHandler {

	public YamlModelDataHandler(IModelDao dao) {
		super(dao, configure(YAMLMapper.builder()
				.addModule(Deserializers.TCVWRITER_MODULE))
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
