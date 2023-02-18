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
package ch.scaille.gui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JTable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.gui.TestObject;
import ch.scaille.gui.TestObjectTableModel;
import ch.scaille.gui.model.views.IListView;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.swing.model.ListModelTableModel;

class ListModelBasicTest {

	private static final IListView<TestObject> VIEW = ListViews.sorted(Comparator.comparingInt(TestObject::getVal));
	static final IListView<TestObject> REVERTED_VIEW = ListViews.sorted((o1, o2) -> o2.getVal() - o1.getVal());

	private static final class EventsCounting implements IListModelListener<TestObject> {
		int editionStartedEvent = 0;
		int editionStoppingEvent = 0;
		int editionStoppedEvent = 0;
		int valueAddedEvent = 0;

		@Override
		public void editionStarted(ListEvent<TestObject> event) {
			editionStartedEvent++;
		}

		@Override
		public void editionStopping(ListEvent<TestObject> event) {
			editionStoppingEvent++;
		}

		@Override
		public void editionStopped(ListEvent<TestObject> event) {
			editionStoppedEvent++;
		}

		@Override
		public void valuesAdded(ListEvent<TestObject> event) {
			valueAddedEvent++;
		}

	}

	private static class TestObjectTableListModel
			extends ListModelTableModel<TestObject, TestObjectTableListModel.Columns> {
		public enum Columns {
			VAL
		}

		int insertCount = 0;

		public TestObjectTableListModel(ListModel<TestObject> model) {
			super(model, Columns.class);
		}

		public int getInsertCount() {
			return insertCount;
		}

		@Override
		public void fireTableRowsInserted(int firstRow, int lastRow) {
			insertCount++;
			super.fireTableRowsInserted(firstRow, lastRow);
		}

		@Override
		protected Object getValueAtColumn(TestObject object, Columns column) {
			return object.getVal();
		}

		@Override
		protected void setValueAtColumn(TestObject object, Columns column, Object value) {
			// nope
		}

	}

	@Test
	void testInsert() {
		final ListModel<TestObject> model = new ListModel<>(VIEW);
		final ListModel<TestObject> childModel = model.child(ListViews.inherited());

		model.insert(new TestObject(1));
		model.insert(new TestObject(3));
		model.insert(new TestObject(2));
		model.insert(new TestObject(4));
		checkModel(model, 1, 2, 3, 4);
		checkModel(childModel, 1, 2, 3, 4);
	}

	private JTable table(ListModel<TestObject> listModel) {
		return new JTable(new TestObjectTableModel(listModel));
	}

	@Test
	void testUpdate() {

		final EventsCounting eventsCounting = new EventsCounting();

		final ListModel<TestObject> model = new ListModel<>(VIEW);
		model.addListener(eventsCounting);
		final ListModel<TestObject> childModel = model.child(ListViews.inherited());
		JTable table = table(model);

		model.insert(new TestObject(1));
		model.insert(new TestObject(3));

		final TestObject toMove = new TestObject(4);
		model.insert(toMove);
		checkModel(model, 1, 3, 4);
		checkModel(childModel, 1, 3, 4);
		checkModel(table, 1, 3, 4);

		model.editValue(toMove, t -> t.setVal(2));
		checkModel(model, 1, 2, 3);
		checkModel(childModel, 1, 2, 3);
		checkModel(table, 1, 2, 3);

		model.editValue(toMove, t -> t.setVal(5));
		checkModel(model, 1, 3, 5);
		checkModel(childModel, 1, 3, 5);
		checkModel(table, 1, 3, 5);

		model.editValue(toMove, t -> t.setVal(0));
		checkModel(model, 0, 1, 3);
		checkModel(childModel, 0, 1, 3);
		checkModel(table, 0, 1, 3);

		assertEquals(3, eventsCounting.valueAddedEvent);
		assertEquals(3, eventsCounting.editionStartedEvent);
		assertEquals(3, eventsCounting.editionStoppingEvent);
		assertEquals(3, eventsCounting.editionStoppedEvent);
	}

	@Test
	void testUpdateChildOnly() {
		final ListModel<TestObject> model = new ListModel<>(VIEW);
		final ListModel<TestObject> childModel = model.child(ListViews
				.sortedFiltered((t1, t2) -> Integer.compare(t2.getVal(), t1.getVal()), t -> t.getVal() % 2 == 0));
		JTable table = table(childModel);

		model.insert(new TestObject(1));
		model.insert(new TestObject(4));
		model.insert(new TestObject(7));

		final TestObject toMove = new TestObject(0);
		model.insert(toMove);
		checkModel(model, 0, 1, 4, 7);
		checkModel(childModel, 4, 0);
		checkModel(table, 4, 0);

		model.editValue(toMove, t -> t.setVal(2));
		checkModel(model, 1, 2, 4, 7);
		checkModel(childModel, 4, 2);
		checkModel(table, 4, 2);

		model.editValue(toMove, t -> t.setVal(5));
		checkModel(model, 1, 4, 5, 7);
		checkModel(childModel, 4);

		model.editValue(toMove, t -> t.setVal(6));
		checkModel(model, 1, 4, 6, 7);
		checkModel(childModel, 6, 4);
		checkModel(table, 6, 4);

		model.editValue(toMove, t -> t.setVal(8));
		checkModel(model, 1, 4, 7, 8);
		checkModel(childModel, 8, 4);
		checkModel(table, 8, 4);

		model.editValue(toMove, t -> t.setVal(2));
		checkModel(model, 1, 2, 4, 7);
		checkModel(childModel, 4, 2);
		checkModel(table, 4, 2);
	}

	@Test
	void testDelete() {
		final ListModel<TestObject> model = new ListModel<>(VIEW);
		final ListModel<TestObject> childModel = model.child(ListViews.inherited());

		model.insert(new TestObject(1));
		final TestObject toAddAndRemove = new TestObject(2);
		model.insert(toAddAndRemove);
		model.insert(new TestObject(3));
		model.remove(toAddAndRemove);

		checkModel(model, 1, 3);
		checkModel(childModel, 1, 3);
	}

	@Test
	void testSearch() {

		final ListModel<TestObject> model = new ListModel<>(VIEW);
		final ListModel<TestObject> childModel = model.child(ListViews
				.sortedFiltered((t1, t2) -> Integer.compare(t2.getVal(), t1.getVal()), t -> t.getVal() % 2 == 0));

		TestObjectTableListModel tableModel = new TestObjectTableListModel(childModel);

		model.findOrCreateAndEdit(new TestObject(1), t -> t.setVal(2));
		model.findOrCreateAndEdit(new TestObject(3), t -> t.setVal(4));

		checkModel(model, 2, 4);
		checkModel(childModel, 4, 2);

		assertEquals(2, tableModel.getInsertCount());

	}

	@Test
	void testChangeSorting() {
		final ListModel<TestObject> model = new ListModel<>(VIEW);
		final ListModel<TestObject> childModel = model.child(ListViews.inherited());

		model.insert(new TestObject(1));
		model.insert(new TestObject(2));
		model.insert(new TestObject(3));

		model.setView(REVERTED_VIEW);

		checkModel(model, 3, 2, 1);
		checkModel(childModel, 3, 2, 1);
	}

	private void checkModel(final ListModel<TestObject> model, final int... expected) {
		assertEquals(expected.length, model.getSize(), () -> "size");
		final int[] current = new int[model.getSize()];
		for (int i = 0; i < current.length; i++) {
			current[i] = model.getValueAt(i).getVal();
		}
		assertEquals(Arrays.toString(expected), Arrays.toString(current));
	}

	private void checkModel(final JTable table, final int... expected) {
		Assertions.assertEquals(expected.length, table.getRowCount(), () -> "size");
		final int[] current = new int[table.getRowCount()];
		for (int i = 0; i < current.length; i++) {
			current[i] = (int) table.getValueAt(i, 0);
		}
		assertEquals(Arrays.toString(expected), Arrays.toString(current));
	}
}
