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
package ch.scaille.gui.mvc.converters;

import ch.scaille.gui.mvc.GuiError;

public class GuiErrorToStringConverter implements IConverter<GuiError, String> {

	private String noError;

	public GuiErrorToStringConverter(String noError) {
		this.noError = noError;
	}

	@Override
	public GuiError convertComponentValueToPropertyValue(final String text) {
		throw new IllegalStateException("Gui error cannot be created for: " + text);
	}

	@Override
	public String convertPropertyValueToComponentValue(final GuiError value) {
		if (value == null) {
			return noError;
		}
		final Object content = value.getContent();
		if (content instanceof Exception) {
			return ((Exception) content).getLocalizedMessage();
		}
		return String.valueOf(content);
	}

}
