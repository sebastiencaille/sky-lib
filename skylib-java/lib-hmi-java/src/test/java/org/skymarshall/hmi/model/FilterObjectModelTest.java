/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.model;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;
import org.skymarshall.hmi.TestObject;
import org.skymarshall.hmi.model.IFilter;
import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.views.IListView;
import org.skymarshall.hmi.model.views.ListView;

public class FilterObjectModelTest extends Assert {

    private static final IListView<TestObject> COMPARATOR    = ListView.sorted(new Comparator<TestObject>() {
                                                                 @Override
                                                                 public int compare(final TestObject o1,
                                                                         final TestObject o2) {
                                                                     return o1.val.compareTo(o2.val);
                                                                 }

                                                             });

    @SuppressWarnings("serial")
    private static final IFilter<TestObject>   FILTER        = new IFilter<TestObject>() {

                                                                 @Override
                                                                 public boolean accept(final TestObject value) {
                                                                     return value.val.intValue() % 2 == 0;
                                                                 }

                                                             };

    @SuppressWarnings("serial")
    private static final IFilter<TestObject>   IMPAIR_FILTER = new IFilter<TestObject>() {

                                                                 @Override
                                                                 public boolean accept(final TestObject value) {
                                                                     return value.val.intValue() % 2 == 1;
                                                                 }

                                                             };

    @Test
    public void testInsert() {
        final ListModel<TestObject> baseModel = new ListModel<TestObject>(ListView.sorted(COMPARATOR));
        final ListModel<TestObject> model = new ListModel<TestObject>(baseModel, ListView.filtered(FILTER));

        baseModel.insert(new TestObject(1));
        baseModel.insert(new TestObject(3));
        baseModel.insert(new TestObject(2));
        baseModel.insert(new TestObject(4));
        checkModel(model, 2, 4);

    }

    @Test
    public void testUpdate() {
        final ListModel<TestObject> baseModel = new ListModel<TestObject>(ListView.sorted(COMPARATOR));
        final ListModel<TestObject> model = new ListModel<TestObject>(baseModel, ListView.filtered(FILTER));

        baseModel.insert(new TestObject(1));
        baseModel.insert(new TestObject(4));

        final TestObject toMove = new TestObject(3);
        baseModel.insert(toMove);

        baseModel.startEditingValue(toMove);
        toMove.val = new Integer(2);
        baseModel.stopEditingValue();

        checkModel(model, 2, 4);

    }

    @Test
    public void testDelete() {
        final ListModel<TestObject> baseModel = new ListModel<TestObject>(ListView.sorted(COMPARATOR));
        final ListModel<TestObject> model = new ListModel<TestObject>(baseModel, ListView.filtered(FILTER));

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
        final ListModel<TestObject> baseModel = new ListModel<TestObject>(ListView.sorted(COMPARATOR));
        final ListModel<TestObject> model = new ListModel<TestObject>(baseModel, ListView.filtered(FILTER));

        baseModel.insert(new TestObject(1));
        baseModel.insert(new TestObject(3));
        baseModel.insert(new TestObject(2));
        baseModel.insert(new TestObject(4));

        model.setView(ListView.filtered(IMPAIR_FILTER));

        checkModel(model, 1, 3);

    }

    private void checkModel(final ListModel<TestObject> model, final int... expected) {
        final int[] current = new int[model.getSize()];
        for (int i = 0; i < model.getSize(); i++) {
            current[i] = model.getValueAt(i).val.intValue();
        }
        assertEquals(Arrays.toString(expected), Arrays.toString(current));
    }
}
