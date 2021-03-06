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
package ch.skymarshall.example.gui.model.impl;

import static ch.skymarshall.gui.model.views.ListViews.sorted;

import ch.skymarshall.example.gui.TestObject;
import ch.skymarshall.gui.model.views.IListView;
import ch.skymarshall.gui.mvc.converters.IConverter;

public interface Converters {

	public static IConverter<Boolean, IListView<TestObject>> booleanToOrder() {
		return ch.skymarshall.gui.mvc.factories.Converters.<IListView<TestObject>>either(
				() -> sorted(TableModelExampleView.NATURAL_ORDER), () -> sorted(TableModelExampleView.REVERSE_ORDER));
	}

}
