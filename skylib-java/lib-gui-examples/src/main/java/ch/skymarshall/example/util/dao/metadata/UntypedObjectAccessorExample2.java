/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.example.util.dao.metadata;

import static ch.skymarshall.example.util.dao.metadata.ADataObject.AN_ATTRIBUTE;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

import ch.skymarshall.util.dao.metadata.DataObjectManager;
import ch.skymarshall.util.dao.metadata.DataObjectManagerFactory;
import ch.skymarshall.util.dao.metadata.UntypedDataObjectManager;
import ch.skymarshall.util.helpers.Log;
import ch.skymarshall.util.text.ArrowIndentationManager;
import ch.skymarshall.util.text.SimpleTextFormatter;
import ch.skymarshall.util.text.TextFormatter;

public interface UntypedObjectAccessorExample2 {

	public static class ASecondDataObject extends ADataObject {

		// Maybe more...

	}

	public static class AThirdDataObject {

		private String anAttribute = "data1";

		public String getAnAttribute() {
			return anAttribute;
		}

		public void setAnAttribute(final String anAttribute) {
			this.anAttribute = anAttribute;
		}

	}

	public static String getAttributeOf(final UntypedDataObjectManager<?> accessor) {
		return accessor.getValueOf(AN_ATTRIBUTE, String.class);
	}

	public static void main(final String[] args) throws IOException {

		try (OutputStream output = Log.streamOf(DataObjectMetaDataExample.class, Level.INFO)) {
			final SimpleTextFormatter<RuntimeException> log = new SimpleTextFormatter<>(
					TextFormatter.safeOutput(output));
			log.setIndentationManager(new ArrowIndentationManager());

			log.appendIndentedLine("Content of do1");
			final ADataObject do1 = new ADataObject();
			do1.setAnAttribute("data1");
			final DataObjectManager<ADataObject> accessor1 = DataObjectManagerFactory.createFor(ADataObject.class, do1);
			log.indented(t -> t.appendIndentedLine(getAttributeOf(accessor1.getUntypedAccessor())));

			log.appendIndentedLine("Content of do2");
			final ASecondDataObject do2 = new ASecondDataObject();
			do2.setAnAttribute("data2");
			final UntypedDataObjectManager<?> accessor2 = DataObjectManagerFactory.createFor(do2);
			log.indented(t -> t.appendIndentedLine(getAttributeOf(accessor2)));

			log.appendIndentedLine("Content of do3");
			final AThirdDataObject do3 = new AThirdDataObject();
			do3.setAnAttribute("data3");
			final UntypedDataObjectManager<?> accessor3 = DataObjectManagerFactory.createFor(do3);
			log.indented(t -> t.appendIndentedLine(getAttributeOf(accessor3)));
		}
	}
}
