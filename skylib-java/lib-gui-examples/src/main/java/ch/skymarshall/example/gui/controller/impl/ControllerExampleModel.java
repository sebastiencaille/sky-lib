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
package ch.skymarshall.example.gui.controller.impl;

import java.util.Comparator;

import ch.skymarshall.example.gui.TestObject;
import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.model.RootListModel;
import ch.skymarshall.gui.model.views.ListViews;
import ch.skymarshall.gui.mvc.GuiController;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;

public class ControllerExampleModel extends ControllerExampleObjectGuiModel {

	private static final Comparator<TestObject> TEST_COMPARATOR = (o1, o2) -> o1.getAFirstValue()
			.compareTo(o2.getAFirstValue());

	public ControllerExampleModel(final GuiController controller) {
		super(controller);
	}

	private final ObjectProperty<String> listSelectedObjectProperty = new ObjectProperty<>("ListObjectProperty",
			propertySupport);

	private final ObjectProperty<String> dynamicListObjectProperty = new ObjectProperty<>("DynamicListObjectProperty",
			propertySupport);

	private final ObjectProperty<TestObject> complexProperty = new ObjectProperty<>("ComplexObject", propertySupport);

	final ListModel<TestObject> tableModel = new RootListModel<>(ListViews.sorted(TEST_COMPARATOR));

	public ObjectProperty<String> getListSelectedObjectProperty() {
		return listSelectedObjectProperty;
	}

	public ObjectProperty<String> getDynamicListObjectProperty() {
		return dynamicListObjectProperty;
	}

	public ObjectProperty<TestObject> getComplexProperty() {
		return complexProperty;
	}

	public ListModel<TestObject> getTableModel() {
		return tableModel;
	}

}
