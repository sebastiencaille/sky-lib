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

import org.skymarshall.util.dao.metadata.AbstractAttributeMetaData;
import org.skymarshall.util.dao.metadata.DataObjectMetaData;
import org.skymarshall.util.text.ArrowIndentationManager;
import org.skymarshall.util.text.TextFormatter;

/**
 * This just shows how to use the DataObjectMetaData...
 *
 * @author Sebastien Caille
 *
 */
public class DataObjectMetaDataExample {

	private DataObjectMetaDataExample() {
	}

	public static void main(final String[] args) throws IOException {

		final TextFormatter log = new TextFormatter(TextFormatter.output(System.out)); // NOSONAR
		log.setIndentationManager(new ArrowIndentationManager());

		final DataObjectMetaData<ADataObject> metadata = new DataObjectMetaData<>(ADataObject.class);
		final ADataObject do1 = new ADataObject();

		log.appendIndentedLine("Class " + ADataObject.class.getName() + " contains the following attributes");
		log.indent();
		for (final AbstractAttributeMetaData<ADataObject> attribute : metadata.getAttributes()) {
			log.appendIndentedLine(attribute.toString());
		}
		log.unindent();

		log.appendIndentedLine("Read/Write access using the DO's MetaData");
		log.indent();
		metadata.getAttribute("AnAttribute").setValueOf(do1, "data1");
		log.appendIndentedLine("anAttribute:" + metadata.getAttribute("AnAttribute").getValueOf(do1));
		log.unindent();

		log.appendIndentedLine("One can also copy the content of the DO...");
		log.indent();
		final ADataObject do2 = new ADataObject();
		metadata.copy(do1, do2);
		log.appendIndentedLine("anAttribute:" + metadata.getAttribute("AnAttribute").getValueOf(do2));
		log.unindent();
	}
}
