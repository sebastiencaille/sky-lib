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

import static org.skymarshall.example.util.dao.metadata.ADataObject.AN_ATTRIBUTE;

import java.io.IOException;

import org.skymarshall.util.dao.metadata.DataObjectManager;
import org.skymarshall.util.dao.metadata.DataObjectManagerFactory;
import org.skymarshall.util.dao.metadata.UntypedDataObjectManager;
import org.skymarshall.util.text.ArrowIndentationManager;
import org.skymarshall.util.text.TextFormatter;

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

	public static String getAttributeOf(final UntypedDataObjectManager<?> _accessor) {
		return _accessor.getValueOf(AN_ATTRIBUTE, String.class);
	}

	public static void main(final String[] args) throws IOException {

		final TextFormatter log = new TextFormatter(TextFormatter.output(System.out)); // NOSONAR
		log.setIndentationManager(new ArrowIndentationManager());

		log.appendIndentedLine("Content of do1");
		log.indent();
		final ADataObject do1 = new ADataObject();
		do1.setAnAttribute("data1");

		final DataObjectManager<ADataObject> accessor1 = DataObjectManagerFactory.createFor(ADataObject.class, do1);
		log.appendIndentedLine(getAttributeOf(accessor1.getUntypedAccessor()));
		log.unindent();

		log.appendIndentedLine("Content of do2");
		log.indent();
		final ASecondDataObject do2 = new ASecondDataObject();
		do2.setAnAttribute("data2");

		final UntypedDataObjectManager<?> accessor2 = DataObjectManagerFactory.createFor(do2);
		log.appendIndentedLine(getAttributeOf(accessor2));
		log.unindent();

		log.appendIndentedLine("Content of do3");
		log.indent();
		final AThirdDataObject do3 = new AThirdDataObject();
		do3.setAnAttribute("data3");
		final UntypedDataObjectManager<?> accessor3 = DataObjectManagerFactory.createFor(do3);
		log.appendIndentedLine(getAttributeOf(accessor3));
		log.unindent();
	}
}
