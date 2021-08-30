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
package ch.skymarshall.gui.mvc.properties;

import ch.skymarshall.gui.mvc.GuiError;
import ch.skymarshall.gui.mvc.GuiModel;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.AbstractProperty.ErrorNotifier;

/**
 * Property containing an error.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public class ErrorProperty extends ObjectProperty<GuiError> implements ErrorNotifier {

	public ErrorProperty(final String name, final GuiModel model) {
		super(name, model, null);
	}

	public ErrorProperty(final String name, final IScopedSupport propertySupport) {
		super(name, propertySupport, null);
	}

	@Override
	public void notifyError(final Object caller, final AbstractProperty property, final GuiError e) {
		setObjectValue(caller, e);
	}

	@Override
	public void clearError(final Object caller, final AbstractProperty property) {
		if (caller == this) {
			return;
		}
		setObjectValue(caller, null);
	}
}
