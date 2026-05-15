import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.webapp.api.validators {
	
	exports ch.scaille.tcwriter.server.validators;
	
	requires transitive jakarta.validation;
    requires org.jspecify;
}