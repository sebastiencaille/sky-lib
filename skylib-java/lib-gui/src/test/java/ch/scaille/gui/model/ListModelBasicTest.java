package ch.scaille.gui.model;

import static java.util.Comparator.comparingInt;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

import javax.swing.JTable;

import org.junit.jupiter.api.Test;

import ch.scaille.gui.TestObject;
import ch.scaille.gui.TestObjectTableModel;
import ch.scaille.gui.model.views.IListView;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.swing.model.ListModelTableModel;

class ListModelBasicTest {

	private static final Comparator<TestObject> COMPARE = comparingInt(TestObject::getVal);
	private static final IListView<TestObject> VIEW = ListViews.sorted(COMPARE);
	private static final IListView<TestObject> REVERSED_VIEW = ListViews.sorted(COMPARE.reversed());

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
		final var model = new ListModel<>(VIEW);
		final var childModel = model.child(ListViews.inherited());

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

		final var eventsCounting = new EventsCounting();

		final var model = new ListModel<>(VIEW);
		model.addListener(eventsCounting);
		final var childModel = model.child(ListViews.inherited());
		final var table = table(model);

		model.insert(new TestObject(1));
		model.insert(new TestObject(3));

		final var toMove = new TestObject(4);
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
		final var model = new ListModel<>(VIEW);
		final var childModel = model.child(ListViews
				.sortedFiltered((t1, t2) -> Integer.compare(t2.getVal(), t1.getVal()), t -> t.getVal() % 2 == 0));
		final var table = table(childModel);

		model.insert(new TestObject(1));
		model.insert(new TestObject(4));
		model.insert(new TestObject(7));

		final var toMove = new TestObject(0);
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
		final var model = new ListModel<>(VIEW);
		final var childModel = model.child(ListViews.inherited());

		model.insert(new TestObject(1));
		final var toAddAndRemove = new TestObject(2);
		model.insert(toAddAndRemove);
		model.insert(new TestObject(3));
		model.remove(toAddAndRemove);

		checkModel(model, 1, 3);
		checkModel(childModel, 1, 3);
	}

	@Test
	void testSearch() {

		final var model = new ListModel<>(VIEW);
		final var childModel = model.child(REVERSED_VIEW);

		final var tableModel = new TestObjectTableListModel(childModel);

		model.findOrCreateAndEdit(new TestObject(1), t -> t.setVal(2));
		model.findOrCreateAndEdit(new TestObject(3), t -> t.setVal(4));

		checkModel(model, 2, 4);
		checkModel(childModel, 4, 2);

		assertEquals(2, tableModel.getInsertCount());
	}

	@Test
	void testChangeSorting() {
		final var model = new ListModel<>(VIEW);
		final var childModel = model.child(ListViews.inherited());

		model.insert(new TestObject(1));
		model.insert(new TestObject(2));
		model.insert(new TestObject(3));

		model.setView(REVERSED_VIEW);

		checkModel(model, 3, 2, 1);
		checkModel(childModel, 3, 2, 1);
	}

	private void checkModel(final ListModel<TestObject> model, final int... expected) {
		assertEquals(expected.length, model.getSize(), "size");
		final var current = model.values().stream().map(TestObject::getVal).toArray();
		assertEquals(Arrays.toString(expected), Arrays.toString(current));
	}

	private void checkModel(final JTable table, final int... expected) {
		assertEquals(expected.length, table.getRowCount(), "size");
		final var current = IntStream.range(0, table.getRowCount())
				.map(i -> (Integer)table.getValueAt(i, 0))
				.toArray();
		assertEquals(Arrays.toString(expected), Arrays.toString(current));
	}
}
