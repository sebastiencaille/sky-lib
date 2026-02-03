package ch.scaille.tcwriter.server.dao;

import ch.scaille.util.persistence.handlers.IStorageDataHandler;
import tools.jackson.databind.ObjectMapper;

public class JsonStorageHandler implements IStorageDataHandler {

    public static final String JSON_MIMETYPE = "application/json";

    private final ObjectMapper mapper;

    public JsonStorageHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String getDefaultMimeType() {
        return JSON_MIMETYPE;
    }

    @Override
    public boolean supports(String extensionOrType) {
        return JSON_MIMETYPE.equals(extensionOrType) || getDefaultExtension().equals(extensionOrType);
    }

    @Override
    public String getDefaultExtension() {
        return "json";
    }

    @Override
    public <T> String encode(Class<T> targetType, T value) {
        return mapper.writeValueAsString(value);
    }

    @Override
    public <T> T decode(String value, Class<T> targetType, T template) {
        return mapper.readValue(value, targetType);
    }

}
