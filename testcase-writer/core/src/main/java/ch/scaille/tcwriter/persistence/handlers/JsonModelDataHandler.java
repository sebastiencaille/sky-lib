package ch.scaille.tcwriter.persistence.handlers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

public class JsonModelDataHandler extends AbstractJacksonDataHandler {

    public JsonModelDataHandler() {
        super(configure(JsonMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build(), DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY))
                .addModule(testCaseWriterModule)
                .build());
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
