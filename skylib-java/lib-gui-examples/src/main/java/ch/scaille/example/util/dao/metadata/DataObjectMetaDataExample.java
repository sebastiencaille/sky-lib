package ch.scaille.example.util.dao.metadata;

import java.io.IOException;
import java.util.logging.Level;

import ch.scaille.util.dao.metadata.DataObjectMetaData;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.text.ArrowIndentationManager;
import ch.scaille.util.text.SimpleTextFormatter;
import ch.scaille.util.text.TextFormatter;

/**
 * This just shows how to use the DataObjectMetaData...
 *
 * @author Sebastien Caille
 *
 */
public class DataObjectMetaDataExample {

	private static final String AN_ATTRIBUTE = "AnAttribute";

	private DataObjectMetaDataExample() {
	}

	public static void main(final String[] args) throws IOException {

		try (var output = Logs.streamOf(DataObjectMetaDataExample.class, Level.INFO)) {
			final var log = new SimpleTextFormatter<>(TextFormatter.safeOutput(output));
			log.setIndentationManager(new ArrowIndentationManager());

			final var metadata = new DataObjectMetaData<>(ADataObject.class);
			final var do1 = new ADataObject();

			log.appendIndentedLine("Class " + ADataObject.class.getName() + " contains the following attributes")
					.indent();
			for (final var attribute : metadata.getAttributes()) {
				log.appendIndentedLine(attribute.toString());
			}
			log.unindent();

			log.appendIndentedLine("Read/Write access using the DO's MetaData").indent();
			metadata.getAttribute(AN_ATTRIBUTE).setValueOf(do1, "data1");
			log.appendIndentedLine(AN_ATTRIBUTE + ":" + metadata.getAttribute(AN_ATTRIBUTE).getValueOf(do1));
			log.unindent();

			log.appendIndentedLine("One can also copy the content of the DO...").indent();
			final var do2 = new ADataObject();
			metadata.copy(do1, do2);
			log.appendIndentedLine(AN_ATTRIBUTE + ":" + metadata.getAttribute(AN_ATTRIBUTE).getValueOf(do2));
			log.unindent();
		}
	}
}
