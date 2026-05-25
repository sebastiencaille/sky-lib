package ch.scaille.tcwriter.persistence.handlers;

import ch.scaille.tcwriter.persistence.handlers.serdeser.Deserializers;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.dataformat.yaml.YAMLMapper;

public class YamlModelMetadataHandler extends AbstractModelDataHandler {

	public YamlModelMetadataHandler() {
		super(configure(YAMLMapper.builder()
				.addModule(Deserializers.TCWRITER_MODULE))
				.activateDefaultTypingAsProperty(BasicPolymorphicTypeValidator.builder().build(), DefaultTyping.JAVA_LANG_OBJECT, null)
				.build());
	}

    @Override
    public <T> T decode(String value, Class<T> targetType, @Nullable T template) {
        final var tree = mapper.readTree(value);
        return mapper.readerFor(targetType). readValue(tree.get("metadata"));
    }
	
	@Override
	public String getDefaultExtension() {
		return "";
	}
	
	@Override
	public String getDefaultMimeType() {
		return METADATA_MIME_TYPE;
	}

	@Override
	public boolean supports(String extensionOrType) {
		return METADATA_MIME_TYPE.equals(extensionOrType);
	}


}
