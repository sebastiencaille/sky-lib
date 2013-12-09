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
package org.skymarshall.example.hmi.controllermodel.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.skymarshall.example.hmi.TestObject;
import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.views.IListView;
import org.skymarshall.hmi.model.views.IListViewOwner;
import org.skymarshall.hmi.mvc.HmiController;

public class TestObjectControllerModelController extends HmiController {

    final ListModel<TestObject>                       model = new ListModel<>(new IListView<TestObject>() {

                                                                @Override
                                                                public int compare(final TestObject o1,
                                                                        final TestObject o2) {
                                                                    return o1.aSecondValue - o2.aSecondValue;
                                                                }

                                                                @Override
                                                                public boolean accept(final TestObject object) {
                                                                    return true;
                                                                }

                                                                @Override
                                                                public void attach(
                                                                        final IListViewOwner<TestObject> owner) {
                                                                    // nope
                                                                }

                                                                @Override
                                                                public void detach(
                                                                        final IListViewOwner<TestObject> owner) {
                                                                    // nope
                                                                }
                                                            });
    private final TestObjectControllerModelFrameModel tableModel;

    public TestObjectControllerModelController() {
        model.insert(new TestObject("Bla", 1));
        model.insert(new TestObject("BlaBla", 2));

        tableModel = new TestObjectControllerModelFrameModel(this, model);
    }

    public ListModel<TestObject> getModel() {
        return model;
    }

    public TestObjectControllerModelFrameModel getTableModel() {
        return tableModel;
    }

    public ActionListener getCommitAction() {
        return new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                tableModel.commit();
                for (final TestObject object : model) {
                    System.out.println(object);
                }
            }
        };
    }

}
