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
package ch.scaille.util.text;

public class CharIndentationManager implements IIndentationManager {

	private final String indentationLevel;

	private String indentation = "";

	public CharIndentationManager() {
		this(' ', 4);
	}

	public CharIndentationManager(final char c, final int length) {
		final var builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			builder.append(c);
		}
		indentationLevel = builder.toString();
	}

	@Override
	public void indent() {
		indentation += indentationLevel;
	}

	@Override
	public void unindent() {
		indentation = indentation.substring(indentationLevel.length());
	}

	@Override
	public String getIndentation() {
		return indentation;
	}

}
