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
package ch.scaille.example.gui.controller.impl;

import java.util.Comparator;

import ch.scaille.example.gui.TestObject;
import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.mvc.properties.ErrorSet;
import ch.scaille.gui.mvc.properties.ObjectProperty;
import ch.scaille.gui.validation.ValidationBinding;

public class ControllerExampleModel extends ControllerExampleObjectGuiModel {

	private static final Comparator<TestObject> TEST_COMPARATOR = (o1, o2) -> o1.getAFirstValue()
			.compareTo(o2.getAFirstValue());

	private final ObjectProperty<String> staticListSelection = new ObjectProperty<>("StaticListSelection", this);

	private final ObjectProperty<String> dynamicListObjectProperty = new ObjectProperty<>("DynamicListObjectProperty",
			this);

	private final ObjectProperty<TestObject> complexProperty = new ObjectProperty<>("ComplexObject", this);

	private final ListModel<TestObject> tableModel = new ListModel<>(ListViews.sorted(TEST_COMPARATOR));

	public ControllerExampleModel(final ModelConfiguration config) {
		super(config.with(ValidationBinding.validator()).with(new ErrorSet("Errors", config.getPropertySupport())));
	}

	public ObjectProperty<String> getStaticListSelectionProperty() {
		return staticListSelection;
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
