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

import ch.skymarshall.example.gui.TestObject;
import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.PropertyGroup;
import ch.skymarshall.gui.mvc.properties.BooleanProperty;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;

public class TableModelExampleModel {

	private final IScopedSupport propertySupport = new ControllerPropertyChangeSupport(this).scoped(this);

	public final BooleanProperty reverseOrder = new BooleanProperty("Order", propertySupport);

	public final BooleanProperty enableFilter = new BooleanProperty("Filter", propertySupport);

	public final ObjectProperty<TestObject> objectSelection = new ObjectProperty<>("Selection", propertySupport);

	public final PropertyGroup listChangers = new PropertyGroup();

	public TableModelExampleModel() {
		listChangers.addProperty(reverseOrder);
		listChangers.addProperty(enableFilter);
	}

	public void setCreated() {
		propertySupport.attachAll();
	}

}
