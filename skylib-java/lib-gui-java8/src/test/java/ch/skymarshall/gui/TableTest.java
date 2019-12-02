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

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import javax.swing.JTable;

import org.junit.Assert;
import org.junit.Test;

import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.model.RootListModel;
import ch.skymarshall.gui.model.views.IListView;
import ch.skymarshall.gui.model.views.ListViews;
import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.GuiModel;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.ListProperty;
import ch.skymarshall.gui.swing.model.ListModelTableModel;

public class TableTest extends Assert {

	private static final IListView<TestObject> VIEW = ListViews.sorted((o1, o2) -> o1.val - o2.val);

	private static class Model extends GuiModel {
		ListProperty<TestObject> selection = new ListProperty<>("Selection", propertySupport);

		public Model(final IScopedSupport support) {
			super(support);
		}
	}

	private enum Columns {
		VAL;
	}

	private static class TestTableModel extends ListModelTableModel<TestObject, Columns> {

		public TestTableModel(final ListModel<TestObject> model) {
			super(model, Columns.class);
		}

		@Override
		protected Object getValueAtColumn(final TestObject object, final Columns column) {
			return null;
		}

		@Override
		protected void setValueAtColumn(final TestObject object, final Columns column, final Object value) {
			// no op
		}
	}

	@Test
	public void testSelectionOnInsert() throws InvocationTargetException, InterruptedException {

		final IScopedSupport support = new ControllerPropertyChangeSupport(this).byContainer(this);
		final Model model = new Model(support);
		support.attachAll();

		final ListModel<TestObject> listModel = new RootListModel<>(VIEW);
		final TestTableModel tableModel = new TestTableModel(listModel);
		final JTable table = new JTable();
		model.selection.bind(new ch.skymarshall.gui.swing.bindings.JTableMultiSelectionBinding<>(table, tableModel));

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
