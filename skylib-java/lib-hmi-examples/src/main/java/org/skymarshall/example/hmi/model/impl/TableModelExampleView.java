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

import static org.skymarshall.hmi.mvc.ModelBindings.detachOnUpdateOf;
import static org.skymarshall.hmi.swing.bindings.SwingBindings.selection;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Comparator;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.skymarshall.example.hmi.TestObject;
import org.skymarshall.example.hmi.TestObjectTableModel;
import org.skymarshall.hmi.model.ChildListModel;
import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.RootListModel;
import org.skymarshall.hmi.model.views.ListViews;

@SuppressWarnings("serial")
public class TableModelExampleView extends JFrame {

	static final Comparator<TestObject> NORMAL_ORDER = (o1, o2) -> o1.getASecondValue() - o2.getASecondValue();
	static final Comparator<TestObject> REVERSE_ORDER = (o1, o2) -> o2.getASecondValue() - o1.getASecondValue();

	private final transient TableModelExampleController controller = new TableModelExampleController();
	private final transient ListModel<TestObject> model;

	public TableModelExampleView() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		model = new RootListModel<>(ListViews.sorted(NORMAL_ORDER));

		final DynamicView view = new DynamicView();
		final ListModel<TestObject> filteredModel = new ChildListModel<>(model, view);

		// We could use a separate filter:
		// > final BoundFilter<TestObject, Boolean> filter = BoundFilter.filter((value,
		// > filtered) -> !filtered || value.aSecondValue % 2 == 0);
		// > final ListModel<TestObject> filteredModel = new ChildListModel<>(model,
		// > filtered(filter));
		final TestObjectTableModel tableModel = new TestObjectTableModel(filteredModel);

		final JTable table = new JTable(tableModel);
		controller.objectSelection.bind(selection(table, tableModel)).addDependency(detachOnUpdateOf(filteredModel));
		getContentPane().add(table, BorderLayout.CENTER);

		controller.reverseOrder.bind(view.reverseOrder());
		controller.enableFilter.bind(view.enableFilter());

		// One can also use a converter to change the model's view, or a
		// BoundComparator, too:
		// > controller.reverseOrder.bind(booleanToOrder()).bind(view(model));

		// Bind the separate filter
		// > controller.enableFilter.bind(filter);

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
