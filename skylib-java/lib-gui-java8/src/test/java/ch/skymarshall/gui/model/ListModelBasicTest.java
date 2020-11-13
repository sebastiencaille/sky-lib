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
package ch.skymarshall.gui.model;

import java.util.Arrays;

import javax.swing.JTable;

import org.junit.Assert;
import org.junit.Test;

import ch.skymarshall.gui.TestObject;
import ch.skymarshall.gui.TestObjectTableModel;
import ch.skymarshall.gui.model.views.IListView;
import ch.skymarshall.gui.model.views.ListViews;

public class ListModelBasicTest extends Assert {

	private static final IListView<TestObject> VIEW = ListViews.sorted((o1, o2) -> o1.val - o2.val);

	private static final IListView<TestObject> REVERTED_VIEW = ListViews.sorted((o1, o2) -> o2.val - o1.val);

	@Test
	public void testInsert() {
		final ListModel<TestObject> model = new ListModel<>(VIEW);
		final ListModel<TestObject> model2 = model.child(ListViews.inherited());

		model.insert(new TestObject(1));
		model.insert(new TestObject(3));
		model.insert(new TestObject(2));
		model.insert(new TestObject(4));
		checkModel(model, 1, 2, 3, 4);
		checkModel(model2, 1, 2, 3, 4);
	}

	private JTable table(ListModel<TestObject> listModel) {
		return new JTable(new TestObjectTableModel(listModel));
	}
	
	@Test
	public void testUpdate() {

		final ListModel<TestObject> model = new ListModel<>(VIEW);
		final ListModel<TestObject> childModel = model.child(ListViews.inherited());
		JTable table = table(model);

		model.insert(new TestObject(1));
		model.insert(new TestObject(3));

		final TestObject toMove = new TestObject(4);
		model.insert(toMove);
		checkModel(model, 1, 3, 4);
		checkModel(childModel, 1, 3, 4);
		checkModel(table, 1, 3, 4);

		model.editValue(toMove, t -> t.val = 2);
		checkModel(model, 1, 2, 3);
		checkModel(childModel, 1, 2, 3);
		checkModel(table, 1, 2, 3);

		model.editValue(toMove, t -> t.val = 5);
		checkModel(model, 1, 3, 5);
		checkModel(childModel, 1, 3, 5);
		checkModel(table, 1, 3, 5);

		model.editValue(toMove, t -> t.val = 0);
		checkModel(model, 0, 1, 3);
		checkModel(childModel, 0, 1, 3);
		checkModel(table, 0, 1, 3);
	}

	@Test
	public void testUpdateChildOnly() {
		final ListModel<TestObject> model = new ListModel<>(VIEW);
		final ListModel<TestObject> childModel = model
				.child(ListViews.sortedFiltered((t1, t2) -> Integer.compare(t2.val, t1.val), t -> t.val % 2 == 0));
		JTable table = table(childModel);

		model.insert(new TestObject(1));
		model.insert(new TestObject(4));
		model.insert(new TestObject(7));

		final TestObject toMove = new TestObject(0);
		model.insert(toMove);
		checkModel(model, 0, 1, 4, 7);
		checkModel(childModel, 4, 0);
		checkModel(table, 4, 0);

		model.editValue(toMove, t -> t.val = 2);
		checkModel(model, 1, 2, 4, 7);
		checkModel(childModel, 4, 2);
		checkModel(table, 4, 2);

		model.editValue(toMove, t -> t.val = 5);
		checkModel(model, 1, 4, 5, 7);
		checkModel(childModel, 4);

		model.editValue(toMove, t -> t.val = 6);
		checkModel(model, 1, 4, 6, 7);
		checkModel(childModel, 6, 4);
		checkModel(table, 6, 4);

		model.editValue(toMove, t -> t.val = 8);
		checkModel(model, 1, 4, 7, 8);
		checkModel(childModel, 8, 4);
		checkModel(table, 8, 4);

		model.editValue(toMove, t -> t.val = 2);
		checkModel(model, 1, 2, 4, 7);
		checkModel(childModel, 4, 2);
		checkModel(table, 4, 2);
	}

	@Test
	public void testDelete() {
		final ListModel<TestObject> model = new ListModel<>(VIEW);
		final ListModel<TestObject> model2 = model.child(ListViews.inherited());

		model.insert(new TestObject(1));
		final TestObject toAddAndRemove = new TestObject(2);
		model.insert(toAddAndRemove);
		model.insert(new TestObject(3));
		model.remove(toAddAndRemove);

		checkModel(model, 1, 3);
		checkModel(model2, 1, 3);
	}

	@Test
	public void testChangeSorting() {
		final ListModel<TestObject> model = new ListModel<>(VIEW);
		final ListModel<TestObject> model2 = model.child(ListViews.inherited());

		model.insert(new TestObject(1));
		model.insert(new TestObject(2));
		model.insert(new TestObject(3));

		model.setView(REVERTED_VIEW);

		checkModel(model, 3, 2, 1);
		checkModel(model2, 3, 2, 1);
	}

	private void checkModel(final ListModel<TestObject> model, final int... expected) {
		assertEquals("size", expected.length, model.getSize());
		final int[] current = new int[model.getSize()];
		for (int i = 0; i < current.length; i++) {
			current[i] = model.getValueAt(i).val;
		}
		assertEquals(Arrays.toString(expected), Arrays.toString(current));
	}

	private void checkModel(final JTable table, final int... expected) {
		assertEquals("size", expected.length, table.getRowCount());
		final int[] current = new int[table.getRowCount()];
		for (int i = 0; i < current.length; i++) {
			current[i] = (int) table.getValueAt(i, 0);
		}
		assertEquals(Arrays.toString(expected), Arrays.toString(current));
	}
}
