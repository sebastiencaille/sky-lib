package ch.scaille.tcwriter.persistence.handlers;

import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.persistence.handlers.serdeser.Deserializers;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ExportReferenceDeserializerHandler;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import tools.jackson.databind.*;
import tools.jackson.databind.cfg.ConstructorDetector;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.datatype.guava.GuavaModule;

import ch.scaille.util.persistence.handlers.IStorageDataHandler;

public abstract class AbstractModelDataHandler implements IStorageDataHandler {

    protected static final String YAML_EXT_1 = "yaml";
    protected static final String YAML_EXT_2 = "yml";
    public static final String YAML_MIME_TYPE = "application/yaml";

    protected static final String JSON_EXT_1 = "json";
    public static final String JSON_MIME_TYPE = "application/json";

    private final IModelDao dao;
    protected final ObjectMapper mapper;

    private final DeserializationProblemHandler referenceHandler = new ExportReferenceDeserializerHandler();

    protected AbstractModelDataHandler(IModelDao dao, ObjectMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    protected static <M extends ObjectMapper, B extends MapperBuilder<M, B>> MapperBuilder<M, B> configure(
            MapperBuilder<M, B> builder) {
        return builder.configure(MapperFeature.USE_ANNOTATIONS, true)
                .changeDefaultVisibility(checker -> checker.withFieldVisibility(Visibility.ANY))
                .constructorDetector(ConstructorDetector.EXPLICIT_ONLY)
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType("ch.scaille.tcwriter.")
                        .allowIfBaseType("java.util.")
                        .allowIfBaseType("com.google.common.collect.")
                        .allowIfSubType("ch.scaille.tcwriter.")
                        .build(), DefaultTyping.NON_FINAL)
                .addModule(new GuavaModule());
    }

    @Override
    public <T> String encode(Class<T> targetClass, T value) {
        return mapper.writerFor(targetClass).writeValueAsString(value);
    }

    @Override
    public <T> T decode(String value, Class<T> targetType, T template) {
        final var config = mapper.deserializationConfig()
                .withHandler(referenceHandler);
        var reader = mapper.readerFor(targetType).with(config);
        if (template instanceof TestCase tcTemplate) {
            reader = reader.with(config.getAttributes().withPerCallAttribute(Deserializers.CONTEXT_DICTIONARY, tcTemplate.getDictionary()));
        }
        return reader.readValue(value);
    }

}
