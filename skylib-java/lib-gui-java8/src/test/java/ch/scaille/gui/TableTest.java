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
package ch.scaille.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JTable;

import org.junit.jupiter.api.Test;

import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.model.views.IListView;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.mvc.ControllerPropertyChangeSupport;
import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.IPropertiesGroup;
import ch.scaille.gui.mvc.properties.ListProperty;
import ch.scaille.gui.swing.factories.SwingBindings;

class TableTest {

	private static final IListView<TestObject> VIEW = ListViews.sorted(Comparator.comparingInt(TestObject::getVal));

	private static class Model extends GuiModel {

		private final ListProperty<TestObject> selection = new ListProperty<>("Selection", this);

		public Model(final ModelConfiguration config) {
			super(config);
		}
	}

	@Test
	void testSelectionOnInsert() throws InvocationTargetException, InterruptedException {

		final IPropertiesGroup support = ControllerPropertyChangeSupport.mainGroup(this);
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
