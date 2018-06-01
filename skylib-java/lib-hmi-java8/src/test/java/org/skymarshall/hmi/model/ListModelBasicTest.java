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
package org.skymarshall.hmi.model;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.skymarshall.hmi.TestObject;
import org.skymarshall.hmi.model.views.IListView;
import org.skymarshall.hmi.model.views.ListViews;

public class ListModelBasicTest extends Assert {

	private static final IListView<TestObject> VIEW = ListViews.sorted((o1, o2) -> o1.val - o2.val);

	private static final IListView<TestObject> REVERTED_VIEW = ListViews.sorted((o1, o2) -> o2.val - o1.val);

	@Test
	public void testInsert() {
		final ListModel<TestObject> model = new RootListModel<>(VIEW);
		final ListModel<TestObject> model2 = new ChildListModel<>(model);

		model.insert(new TestObject(1));
		model.insert(new TestObject(3));
		model.insert(new TestObject(2));
		model.insert(new TestObject(4));
		checkModel(model, 1, 2, 3, 4);
		checkModel(model2, 1, 2, 3, 4);
	}

	@Test
	public void testUpdate() {
		final ListModel<TestObject> model = new RootListModel<>(VIEW);
		final ListModel<TestObject> childModel = new ChildListModel<>(model);

		model.insert(new TestObject(1));
		model.insert(new TestObject(3));

		final TestObject toMove = new TestObject(4);
		model.insert(toMove);
		checkModel(model, 1, 3, 4);
		checkModel(childModel, 1, 3, 4);

		model.startEditingValue(toMove);
		toMove.val = 2;
		model.stopEditingValue();

		checkModel(model, 1, 2, 3);
		checkModel(childModel, 1, 2, 3);
	}

	@Test
	public void testDelete() {
		final ListModel<TestObject> model = new RootListModel<>(VIEW);
		final ListModel<TestObject> model2 = new ChildListModel<>(model);

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
		final ListModel<TestObject> model = new RootListModel<>(VIEW);
		final ListModel<TestObject> model2 = new ChildListModel<>(model);

		model.insert(new TestObject(1));
		model.insert(new TestObject(2));
		model.insert(new TestObject(3));

		model.setView(REVERTED_VIEW);

		checkModel(model, 3, 2, 1);
		checkModel(model2, 3, 2, 1);
	}

	private void checkModel(final ListModel<TestObject> model, final int... expected) {
		final int[] current = new int[model.getSize()];
		for (int i = 0; i < model.getSize(); i++) {
			current[i] = model.getValueAt(i).val;
		}
		assertEquals(Arrays.toString(expected), Arrays.toString(current));
	}
}
