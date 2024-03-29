package ch.scaille.example.util.dao.metadata;

import static ch.scaille.example.util.dao.metadata.ADataObject.AN_ATTRIBUTE;

import java.io.IOException;
import java.util.logging.Level;

import ch.scaille.util.dao.metadata.DataObjectManagerFactory;
import ch.scaille.util.dao.metadata.UntypedDataObjectManager;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.text.ArrowIndentationManager;
import ch.scaille.util.text.SimpleTextFormatter;
import ch.scaille.util.text.TextFormatter;

public interface UntypedObjectAccessorExample2 {

	class ASecondDataObject extends ADataObject {

		// Maybe more...

	}

	class AThirdDataObject {

		private String anAttribute = "data1";

		public String getAnAttribute() {
			return anAttribute;
		}

		public void setAnAttribute(final String anAttribute) {
			this.anAttribute = anAttribute;
		}

	}

	static String getAttributeOf(final UntypedDataObjectManager accessor) {
		return accessor.getValueOf(AN_ATTRIBUTE, String.class);
	}

	static void main(final String[] args) throws IOException {

		try (var output = Logs.streamOf(DataObjectMetaDataExample.class, Level.INFO)) {
			final var log = new SimpleTextFormatter<>(TextFormatter.safeOutput(output));
			log.setIndentationManager(new ArrowIndentationManager());

			log.appendIndentedLine("Content of data object 1");
			final var do1 = new ADataObject();
			do1.setAnAttribute("data1");
			final var accessor1 = DataObjectManagerFactory.createFor(ADataObject.class, do1);
			log.indented(t -> t.appendIndentedLine(getAttributeOf(accessor1.getUntypedAccessor())));

			log.appendIndentedLine("Content of data object 2");
			final var do2 = new ASecondDataObject();
			do2.setAnAttribute("data2");
			final var accessor2 = DataObjectManagerFactory.createFor(do2);
			log.indented(t -> t.appendIndentedLine(getAttributeOf(accessor2)));

			log.appendIndentedLine("Content of data object 3");
			final var do3 = new AThirdDataObject();
			do3.setAnAttribute("data3");
			final var accessor3 = DataObjectManagerFactory.createFor(do3);
			log.indented(t -> t.appendIndentedLine(getAttributeOf(accessor3)));
		}
	}
}
