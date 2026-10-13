package ch.scaille.tcwriter.server.validators;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.UnwrapByDefault;
import jakarta.validation.valueextraction.ValueExtractor;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@UnwrapByDefault
public class JsonNullableValueExtractor
    implements ValueExtractor<JsonNullable<@ExtractedValue ?>> {

    @Override
    public void extractValues(JsonNullable<?> originalValue,
                              ValueExtractor.ValueReceiver receiver) {
        receiver.value(null, originalValue.orElse(null));
    }
}