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
package ch.scaille.example.util.dao.metadata;

import static ch.scaille.example.util.dao.metadata.ADataObject.AN_ATTRIBUTE;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;

import ch.scaille.util.dao.metadata.DataObjectAttribute;
import ch.scaille.util.dao.metadata.DataObjectManagerFactory;
import ch.scaille.util.dao.metadata.DataObjectMetaData;
import ch.scaille.util.dao.metadata.UntypedDataObjectManager;
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

		try (Writer output = Logs.streamOf(DataObjectMetaDataExample.class, Level.INFO)) {
			final SimpleTextFormatter<RuntimeException> log = new SimpleTextFormatter<>(
					TextFormatter.safeOutput(output));
			log.setIndentationManager(new ArrowIndentationManager());

			final ADataObject do1 = new ADataObject();
			final DataObjectMetaData<ADataObject> metadata = new DataObjectMetaData<>(ADataObject.class);
			log.appendIndentedLine("One can create a Data Object Accessor either from the Meta Data");
			final UntypedDataObjectManager accessor0 = metadata.createUntypedAccessorTo(do1);
			log.indented(t -> t.appendIndentedLine(accessor0.toString()));

			log.appendIndentedLine("Or from a factory");
			final UntypedDataObjectManager doAccessor = DataObjectManagerFactory.createFor(do1);
			log.indented(t -> t.appendIndentedLine(doAccessor.toString()));

			log.appendIndentedLine("Read/Write access using the DO's Accessor");
			doAccessor.setValueOf(AN_ATTRIBUTE, "data1");
			log.indented(t -> t.appendIndentedLine(AN_ATTRIBUTE + ":" + doAccessor.getValueOf(AN_ATTRIBUTE)));

			log.appendIndentedLine("Read/Write access using the DO's Attribute Accessor");
			final DataObjectAttribute attribAccessor = doAccessor.getAttributeAccessor(AN_ATTRIBUTE);
			attribAccessor.setValue("data2");
			log.indented(t -> t.appendIndentedLine(AN_ATTRIBUTE + ":" + attribAccessor.getValue()));

			log.appendIndentedLine("One can also copy the content of the DO...");
			final ADataObject do2 = new ADataObject();
			doAccessor.copyInto(do2);
			log.indented(t -> t
					.appendIndentedLine(AN_ATTRIBUTE + ":" + metadata.getAttribute(AN_ATTRIBUTE).getValueOf(do2)));
		}
	}
}
