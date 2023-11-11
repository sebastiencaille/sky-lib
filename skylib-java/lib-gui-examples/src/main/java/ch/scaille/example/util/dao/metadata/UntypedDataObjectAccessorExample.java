package ch.scaille.example.util.dao.metadata;

import static ch.scaille.example.util.dao.metadata.ADataObject.AN_ATTRIBUTE;

import java.io.IOException;
import java.util.logging.Level;

import ch.scaille.util.dao.metadata.DataObjectManagerFactory;
import ch.scaille.util.dao.metadata.DataObjectMetaData;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.text.ArrowIndentationManager;
import ch.scaille.util.text.SimpleTextFormatter;
import ch.scaille.util.text.TextFormatter;

/**
 * This just shows how to use the UntypedDataObjectAccessor...
 *
 * @author Sebastien Caille
 *
 */
public interface UntypedDataObjectAccessorExample {

	static void main(final String[] args) throws IOException {

		try (var output = Logs.streamOf(DataObjectMetaDataExample.class, Level.INFO)) {
			final var log = new SimpleTextFormatter<>(TextFormatter.safeOutput(output));
			log.setIndentationManager(new ArrowIndentationManager());

			final var do1 = new ADataObject();
			final var metadata = new DataObjectMetaData<>(ADataObject.class);
			log.appendIndentedLine("One can create a Data Object Accessor either from the Meta Data");
			final var accessor0 = metadata.createUntypedAccessorTo(do1);
			log.indented(t -> t.appendIndentedLine(accessor0.toString()));

			log.appendIndentedLine("Or from a factory");
			final var doAccessor = DataObjectManagerFactory.createFor(do1);
			log.indented(t -> t.appendIndentedLine(doAccessor.toString()));

			log.appendIndentedLine("Read/Write access using the DO's Accessor");
			doAccessor.setValueOf(AN_ATTRIBUTE, "data1");
			log.indented(t -> t.appendIndentedLine(AN_ATTRIBUTE + ":" + doAccessor.getValueOf(AN_ATTRIBUTE)));

			log.appendIndentedLine("Read/Write access using the DO's Attribute Accessor");
			final var attribAccessor = doAccessor.getAttributeAccessor(AN_ATTRIBUTE);
			attribAccessor.setValue("data2");
			log.indented(t -> t.appendIndentedLine(AN_ATTRIBUTE + ":" + attribAccessor.getValue()));

			log.appendIndentedLine("One can also copy the content of the DO...");
			final var do2 = new ADataObject();
			doAccessor.copyInto(do2);
			log.indented(t -> t
					.appendIndentedLine(AN_ATTRIBUTE + ":" + metadata.getAttribute(AN_ATTRIBUTE).getValueOf(do2)));
		}
	}
}
