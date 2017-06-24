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
/*
 * Copyright (c) 2008, Caille Sebastien
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above Copyrightnotice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above Copyrightnotice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of the owner nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE CopyrightHOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE CopyrightOWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.skymarshall.example.util.dao.metadata;

import java.io.IOException;

import org.skymarshall.util.dao.metadata.DataObjectAttribute;
import org.skymarshall.util.dao.metadata.DataObjectManager;
import org.skymarshall.util.dao.metadata.DataObjectManagerFactory;
import org.skymarshall.util.dao.metadata.DataObjectMetaData;
import org.skymarshall.util.text.ArrowIndentationManager;
import org.skymarshall.util.text.TextFormatter;

/**
 * This just shows how to use the DataObjectAccessor...
 *
 * @author Sebastien Caille
 *
 */
public interface TypedDataObjectAccessorExample {

	public static void main(final String[] args) throws IOException {

		final TextFormatter log = new TextFormatter(TextFormatter.output(System.out)); // NOSONAR
		log.setIndentationManager(new ArrowIndentationManager());

		final ADataObject do1 = new ADataObject();
		final DataObjectMetaData<ADataObject> metadata = new DataObjectMetaData<>(ADataObject.class);

		log.appendIndentedLine("One can create a Data Object Accessor either from the Meta Data");
		log.indent();
		final DataObjectManager<ADataObject> accessor0 = metadata.createAccessorTo(do1);
		log.appendIndentedLine(accessor0.toString());
		log.unindent();

		log.appendIndentedLine("Or from a factory");
		log.indent();
		final DataObjectManager<ADataObject> doAccessor = DataObjectManagerFactory.createFor(ADataObject.class, do1);
		log.appendIndentedLine(doAccessor.toString());
		log.unindent();

		log.appendIndentedLine("Read/Write access using the DO's Accessor");
		log.indent();
		doAccessor.setValueOf("AnAttribute", "data1");
		log.appendIndentedLine("anAttribute:" + doAccessor.getValueOf("AnAttribute"));
		log.unindent();

		log.appendIndentedLine("Read/Write access using the DO's Attribute Accessor");
		log.indent();
		final DataObjectAttribute attribAccessor = doAccessor.getAttributeAccessor("AnAttribute");
		attribAccessor.setValue("data2");
		log.appendIndentedLine("anAttribute:" + attribAccessor.getValue());
		log.unindent();

		log.appendIndentedLine("One can also copy the content of the DO...");
		log.indent();
		final ADataObject do2 = new ADataObject();
		doAccessor.copyInto(do2);
		log.appendIndentedLine("anAttribute:" + metadata.getAttribute("AnAttribute").getValueOf(do2));
		log.unindent();

	}
}
