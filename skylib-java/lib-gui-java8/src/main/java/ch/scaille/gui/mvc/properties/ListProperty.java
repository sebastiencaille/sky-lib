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
package ch.scaille.gui.mvc.properties;

import java.util.List;
import java.util.function.Consumer;

import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.IScopedSupport;

/**
 * Property containing a list of Objects.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> type of the contained objects
 */
public class ListProperty<T> extends ObjectProperty<List<T>> {

	public ListProperty(final String name, final GuiModel model) {
		super(name, model);
	}

	public ListProperty(final String name, final IScopedSupport propertySupport) {
		super(name, propertySupport);
	}

	@SafeVarargs
	@Override
	public final ListProperty<T> configureTyped(final Consumer<AbstractTypedProperty<List<T>>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
	}

}
