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
package ch.skymarshall.example.gui.controllermodel.impl;

import static ch.skymarshall.gui.swing.SwingHelper.actionListener;

import java.awt.event.ActionListener;

import ch.skymarshall.example.gui.TestObject;
import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.model.RootListModel;
import ch.skymarshall.gui.model.views.ListViews;
import ch.skymarshall.gui.mvc.GuiController;

public class TestObjectControllerModelController extends GuiController {

	final ListModel<TestObject> model = new RootListModel<>(
			ListViews.sorted((o1, o2) -> o1.getASecondValue() - o2.getASecondValue()));
	private final TestObjectControllerModelFrameModel tableModel;

	public TestObjectControllerModelController() {
		model.insert(new TestObject("Foo", 1));
		model.insert(new TestObject("Bar", 2));

		tableModel = new TestObjectControllerModelFrameModel(this, model);
	}

	public ListModel<TestObject> getModel() {
		return model;
	}

	public TestObjectControllerModelFrameModel getTableModel() {
		return tableModel;
	}

	public ActionListener getCommitAction() {
		return actionListener(e -> {
			tableModel.commit();
			model.forEach(System.out::println); // NOSONAR
		});
	}

}
