/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.example.hmi.model.impl;

import org.skymarshall.example.hmi.TestObject;
import org.skymarshall.hmi.model.views.IListView;
import org.skymarshall.hmi.model.views.ListViews;
import org.skymarshall.hmi.mvc.converters.AbstractBooleanConverter;

public class Converters {

	public static AbstractBooleanConverter<IListView<TestObject>> booleanToFilter() {
		return AbstractBooleanConverter.<IListView<TestObject>>either(
				() -> ListViews.filtered(TableModelExampleView.FILTER), () -> ListViews.<TestObject>inherited());
	}

	public static AbstractBooleanConverter<IListView<TestObject>> booleanToOrder() {
		return AbstractBooleanConverter.<IListView<TestObject>>either(
				() -> ListViews.sorted(TableModelExampleView.NORMAL_ORDER),
				() -> ListViews.sorted(TableModelExampleView.REVERSE_ORDER));
	}

}
