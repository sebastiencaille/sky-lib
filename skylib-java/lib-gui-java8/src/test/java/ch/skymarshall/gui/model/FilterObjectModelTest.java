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
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;
import ch.skymarshall.gui.TestObject;
import ch.skymarshall.gui.model.ChildListModel;
import ch.skymarshall.gui.model.IEdition;
import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.model.RootListModel;
import ch.skymarshall.gui.model.views.IListView;
import ch.skymarshall.gui.model.views.ListViews;

public class FilterObjectModelTest extends Assert {

	private static final IListView<TestObject> COMPARATOR = ListViews.sorted((o1, o2) -> o1.val - o2.val);

	private static final Predicate<TestObject> EVEN_FILTER = value -> value.val % 2 == 0;

	private static final Predicate<TestObject> ODD_FILTER = value -> value.val % 2 == 1;

	@Test
	public void testInsert() {
		final ListModel<TestObject> baseModel = new RootListModel<>(ListViews.sorted(COMPARATOR));
		final ListModel<TestObject> model = new ChildListModel<>(baseModel, ListViews.filtered(EVEN_FILTER));

		baseModel.insert(new TestObject(1));
		baseModel.insert(new TestObject(3));
		baseModel.insert(new TestObject(2));
		baseModel.insert(new TestObject(4));
		checkModel(model, 2, 4);

	}

	@Test
	public void testFind() {
		final ListModel<TestObject> baseModel = new RootListModel<>(ListViews.sorted(COMPARATOR));

		baseModel.insert(new TestObject(1));
		baseModel.insert(new TestObject(4));
		assertSame(baseModel.getValueAt(0), baseModel.find(new TestObject(1)));
		assertSame(baseModel.getValueAt(1), baseModel.find(new TestObject(4)));
	}

	@Test
	public void testUpdate() {
		final ListModel<TestObject> baseModel = new RootListModel<>(ListViews.sorted(COMPARATOR));
		final ListModel<TestObject> model = new ChildListModel<>(baseModel, ListViews.filtered(EVEN_FILTER));

		baseModel.insert(new TestObject(1));
		baseModel.insert(new TestObject(4));

		final TestObject toMove = new TestObject(3);
		baseModel.insert(toMove);

		baseModel.startEditingValue(toMove);
		toMove.val = 2;
		baseModel.stopEditingValue();

		// model is filtered
		checkModel(model, 2, 4);

	}

	@Test
	public void testFindForEdition() {
		final ListModel<TestObject> baseModel = new RootListModel<>(ListViews.sorted(COMPARATOR));

		baseModel.insert(new TestObject(1));
		baseModel.insert(new TestObject(4));

		try (IEdition<TestObject> edition = baseModel.findForEdition(new TestObject(1))) {
			assertSame(baseModel.getValueAt(0), edition.edited());
			edition.edited().val = 5;
		}
		checkModel(baseModel, 4, 5);
	}

	@Test
	public void testFindOrCreateForEdition() {
		final ListModel<TestObject> baseModel = new RootListModel<>(ListViews.sorted(COMPARATOR));

		baseModel.insert(new TestObject(4));

		try (IEdition<TestObject> edition = baseModel.findOrCreateForEdition(new TestObject(1))) {
			edition.edited().val = 5;
		}
		checkModel(baseModel, 4, 5);

		try (final IEdition<TestObject> edition2 = baseModel.findOrCreateForEdition(new TestObject(5))) {
			edition2.edited().val = 3;
		}
		checkModel(baseModel, 3, 4);
	}

	@Test
	public void testDelete() {
		final ListModel<TestObject> baseModel = new RootListModel<>(ListViews.sorted(COMPARATOR));
		final ListModel<TestObject> model = new ChildListModel<>(baseModel, ListViews.filtered(EVEN_FILTER));

		model.insert(new TestObject(1));
		final TestObject toAddAndRemove = new TestObject(2);
		model.insert(toAddAndRemove);
		model.insert(new TestObject(3));
		model.insert(new TestObject(4));
		model.remove(toAddAndRemove);

		checkModel(model, 4);
	}

	@Test
	public void testChangeFilter() {
		final ListModel<TestObject> baseModel = new RootListModel<>(ListViews.sorted(COMPARATOR));
		final ListModel<TestObject> model = new ChildListModel<>(baseModel, ListViews.filtered(EVEN_FILTER));

		baseModel.insert(new TestObject(1));
		baseModel.insert(new TestObject(3));
		baseModel.insert(new TestObject(2));
		baseModel.insert(new TestObject(4));

		model.setView(ListViews.filtered(ODD_FILTER));

		checkModel(model, 1, 3);

	}

	private void checkModel(final ListModel<TestObject> model, final int... expected) {
		final int[] current = new int[model.getSize()];
		for (int i = 0; i < model.getSize(); i++) {
			current[i] = model.getValueAt(i).val;
		}
		assertEquals(Arrays.toString(expected), Arrays.toString(current));
	}
}
