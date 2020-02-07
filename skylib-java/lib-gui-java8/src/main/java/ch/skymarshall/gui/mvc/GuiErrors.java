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
package ch.skymarshall.gui.mvc;

public class GuiErrors {
	private GuiErrors() {
	}

	/**
	 * Error container
	 *
	 * @author Sebastien Caille
	 *
	 */
	public static class GuiError {
		private final Object source;
		private final Object content;
		private final String message;

		public GuiError(final Object source, final String message, final Object content) {
			this.source = source;
			this.message = message;
			this.content = content;
		}

		public Object getSource() {
			return source;
		}

		public Object getContent() {
			return content;
		}

		public String getMessage() {
			return message;
		}
	}

	public static GuiError fromException(final Object source, final Exception e) {
		return new GuiError(source, e.getMessage(), e);
	}
}
