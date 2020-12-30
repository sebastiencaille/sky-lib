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

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

import ch.skymarshall.util.dao.metadata.AbstractAttributeMetaData;
import ch.skymarshall.util.dao.metadata.DataObjectMetaData;
import ch.skymarshall.util.helpers.Log;
import ch.skymarshall.util.text.ArrowIndentationManager;
import ch.skymarshall.util.text.SimpleTextFormatter;
import ch.skymarshall.util.text.TextFormatter;

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

		try (OutputStream output = Log.streamOf(DataObjectMetaDataExample.class, Level.INFO)) {
			final SimpleTextFormatter<RuntimeException> log = new SimpleTextFormatter<>(
					TextFormatter.safeOutput(output));
			log.setIndentationManager(new ArrowIndentationManager());

			final DataObjectMetaData<ADataObject> metadata = new DataObjectMetaData<>(ADataObject.class);
			final ADataObject do1 = new ADataObject();

			log.appendIndentedLine("Class " + ADataObject.class.getName() + " contains the following attributes")
					.indent();
			for (final AbstractAttributeMetaData<ADataObject> attribute : metadata.getAttributes()) {
				log.appendIndentedLine(attribute.toString());
			}
			log.unindent();

			log.appendIndentedLine("Read/Write access using the DO's MetaData").indent();
			metadata.getAttribute(AN_ATTRIBUTE).setValueOf(do1, "data1");
			log.appendIndentedLine(AN_ATTRIBUTE + ":" + metadata.getAttribute(AN_ATTRIBUTE).getValueOf(do1));
			log.unindent();

			log.appendIndentedLine("One can also copy the content of the DO...").indent();
			final ADataObject do2 = new ADataObject();
			metadata.copy(do1, do2);
			log.appendIndentedLine(AN_ATTRIBUTE + ":" + metadata.getAttribute(AN_ATTRIBUTE).getValueOf(do2));
			log.unindent();
		}
	}
}
