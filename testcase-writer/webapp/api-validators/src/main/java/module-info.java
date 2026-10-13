import ch.scaille.tcwriter.server.validators.JsonNullableValueExtractor;
import jakarta.validation.valueextraction.ValueExtractor;
import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.webapp.api.validators {
	
	exports ch.scaille.tcwriter.server.validators;
	
	requires transitive jakarta.validation;
    requires org.jspecify;
    requires org.openapitools.jackson.nullable;

    provides ValueExtractor with JsonNullableValueExtractor;
}