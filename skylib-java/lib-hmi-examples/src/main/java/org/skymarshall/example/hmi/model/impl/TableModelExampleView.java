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
package org.skymarshall.example.hmi.model.impl;

import static org.skymarshall.hmi.swing.bindings.SwingBindings.selection;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Comparator;
import java.util.function.Predicate;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.skymarshall.example.hmi.TestObject;
import org.skymarshall.example.hmi.TestObjectTableModel;
import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.ListModelBindings;
import org.skymarshall.hmi.model.views.ListViews;
import org.skymarshall.hmi.mvc.ModelBindings;
import org.skymarshall.hmi.swing.bindings.SwingBindings;

@SuppressWarnings("serial")
public class TableModelExampleView extends JFrame {

	static final Comparator<TestObject> NORMAL_ORDER = (o1, o2) -> o1.aSecondValue - o2.aSecondValue;
	static final Comparator<TestObject> REVERSE_ORDER = (o1, o2) -> o2.aSecondValue - o1.aSecondValue;
	static final Predicate<TestObject> FILTER = (value -> value.aSecondValue % 2 == 0);

	private final TableModelExampleController controller = new TableModelExampleController();
	private final ListModel<TestObject> model;

	public TableModelExampleView() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		model = new ListModel<>(ListViews.sorted(NORMAL_ORDER));

		final ListModel<TestObject> filteredModel = new ListModel<>(model,
				ListViews.filtered(TableModelExampleView.FILTER));
		final TestObjectTableModel tableModel = new TestObjectTableModel(filteredModel);

		final JTable table = new JTable(tableModel);
		controller.objectSelection.bind(SwingBindings.selection(table, tableModel))
				.addDependency(ModelBindings.detachOnUpdateOf(filteredModel));
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
		controller.enableFilter.bind(selection(filter));
		optionsPanel.add(filter);
	}

	private void addRevOrder(final JPanel optionsPanel) {
		final JCheckBox reverse = new JCheckBox("Rev. Order");
		controller.reverseOrder.bind(selection(reverse));
		optionsPanel.add(reverse);
	}
}
