package ch.scaille.tcwriter.persistence.handlers.serdeser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.node.StringNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static ch.scaille.tcwriter.persistence.handlers.serdeser.Deserializers.CONTEXT_ALL_REFERENCES;

public class ExportReferenceDeserializerHandler extends DeserializationProblemHandler {
        @Override
        public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, ValueDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws JacksonException {
            var references = (List<ExportReference<?, ?>>) ctxt.getAttribute(CONTEXT_ALL_REFERENCES);
            if (references == null) {
                references = new ArrayList<>();
                ctxt.setAttribute(CONTEXT_ALL_REFERENCES, references);
            }
            final Optional<ReferenceHandler<?, ?>> propertyHandler = Deserializers.getTestCaseHandler(beanOrClass, propertyName).map(Function.identity());
            final var safeHandler = propertyHandler.or(() -> Deserializers.getDictionaryHandler(beanOrClass, propertyName));

            if (safeHandler.isEmpty()) {
                return false;
            }
            references.add(safeHandler.get().of(beanOrClass, ((StringNode) p.readValueAsTree().get("ref")).asString()));
            return true;
        }


}