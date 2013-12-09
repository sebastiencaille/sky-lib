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
package org.skymarshall.example.hmi.model.impl;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Comparator;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.skymarshall.example.hmi.TestObject;
import org.skymarshall.example.hmi.TestObjectTableModel;
import org.skymarshall.hmi.model.IFilter;
import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.ListModelBindings;
import org.skymarshall.hmi.model.views.ListView;
import org.skymarshall.hmi.mvc.Actions;
import org.skymarshall.hmi.swing17.bindings.SwingBindings;

@SuppressWarnings("serial")
public class TableModelExampleView extends JFrame {

    private static class IntOrder implements
            Comparator<TestObject> {

        @Override
        public int compare(final TestObject o1, final TestObject o2) {
            return o1.aSecondValue - o2.aSecondValue;
        }

    }

    private static class IntReverseOrder implements
            Comparator<TestObject> {

        @Override
        public int compare(final TestObject o1, final TestObject o2) {
            return o2.aSecondValue - o1.aSecondValue;
        }

    }

    private static class Filter implements
            IFilter<TestObject> {

        @Override
        public boolean accept(final TestObject value) {
            return value.aSecondValue % 2 == 0;
        }

    }

    static final IntOrder                     NORMAL_ORDER  = new IntOrder();
    static final IntReverseOrder              REVERSE_ORDER = new IntReverseOrder();
    static final Filter                       FILTER        = new Filter();

    private final TableModelExampleController controller    = new TableModelExampleController();
    private final ListModel<TestObject>       model;

    public TableModelExampleView() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        model = new ListModel<TestObject>(ListView.sorted(NORMAL_ORDER));

        final ListModel<TestObject> filteredModel = new ListModel<TestObject>(model,
                ListView.filtered(TableModelExampleView.FILTER));
        final TestObjectTableModel tableModel = new TestObjectTableModel(filteredModel);

        final JTable table = new JTable(tableModel);
        controller.objectSelection.bind(SwingBindings.selection(table, tableModel));
        controller.listChangers.addAction(Actions.restoreAfterUpdate(controller.objectSelection));
        getContentPane().add(table, BorderLayout.CENTER);

        controller.reverseOrder.bind(Converters.booleanToOrder()).bind(ListModelBindings.view(model));
        controller.enableFilter.bind(Converters.booleanToFilter()).bind(ListModelBindings.view(filteredModel));

        final JPanel optionsPanel = new JPanel(new FlowLayout());
        addRevOrder(optionsPanel);
        addFilter(optionsPanel);
        getContentPane().add(optionsPanel, BorderLayout.SOUTH);

        controller.setCreated();

        model.insert(new TestObject("One", 1));
        model.insert(new TestObject("Two", 2));
        model.insert(new TestObject("Three", 3));
        model.insert(new TestObject("Four", 4));

        validate();
        pack();
    }

    private void addFilter(final JPanel optionsPanel) {
        final JCheckBox filter = new JCheckBox("Filter");
        controller.enableFilter.bind(SwingBindings.state(filter));
        optionsPanel.add(filter);
    }

    private void addRevOrder(final JPanel optionsPanel) {
        final JCheckBox reverse = new JCheckBox("Rev. Order");
        controller.reverseOrder.bind(SwingBindings.state(reverse));
        optionsPanel.add(reverse);
    }
}
