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
package ch.skymarshall.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import javax.swing.JTable;

import org.junit.jupiter.api.Test;

import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.model.views.IListView;
import ch.skymarshall.gui.model.views.ListViews;
import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.GuiModel;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.ListProperty;
import ch.skymarshall.gui.swing.factories.SwingBindings;

class TableTest {

	private static final IListView<TestObject> VIEW = ListViews.sorted((o1, o2) -> o1.getVal() - o2.getVal());

	private static class Model extends GuiModel {
		
		private final ListProperty<TestObject> selection = new ListProperty<>("Selection", this);

		public Model(final ModelConfiguration config) {
			super(config);
		}
	}

	@Test
	void testSelectionOnInsert() throws InvocationTargetException, InterruptedException {

		final IScopedSupport support = new ControllerPropertyChangeSupport(this).scoped(this);
		final Model model = new Model(GuiModel.with(support));
		support.attachAll();

		final ListModel<TestObject> listModel = new ListModel<>(VIEW);
		final TestObjectTableModel tableModel = new TestObjectTableModel(listModel);
		final JTable table = new JTable(tableModel);
		model.selection.bind(SwingBindings.multipleSelection(table, tableModel));

		final TestObject object1 = new TestObject(1);
		final TestObject object3 = new TestObject(3);
		final TestObject object5 = new TestObject(5);

		EventQueue.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				listModel.insert(object1);
				listModel.insert(object3);
				listModel.insert(object5);
				model.selection.setValue(this, Collections.singletonList(object3));
				listModel.insert(new TestObject(2));
				assertEquals(1, model.selection.getValue().size()); // NOSONAR
				listModel.insert(new TestObject(4));
				assertEquals(1, model.selection.getValue().size()); // NOSONAR

			}
		});

	}
}
