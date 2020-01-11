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
package ch.skymarshall.example.gui.model.impl;

import static ch.skymarshall.gui.mvc.factories.BindingDependencies.detachOnUpdateOf;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.selected;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.selection;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Comparator;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.skymarshall.example.gui.TestObject;
import ch.skymarshall.example.gui.TestObjectTableModel;
import ch.skymarshall.gui.model.ChildListModel;
import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.model.RootListModel;
import ch.skymarshall.gui.model.views.ListViews;
import ch.skymarshall.gui.swing.ContributionTableColumn;
import ch.skymarshall.gui.swing.ContributionTableColumnModel;

@SuppressWarnings("serial")
public class TableModelExampleView extends JFrame {

	static final Comparator<TestObject> NATURAL_ORDER = (o1, o2) -> o1.getASecondValue() - o2.getASecondValue();
	static final Comparator<TestObject> REVERSE_ORDER = (o1, o2) -> o2.getASecondValue() - o1.getASecondValue();

	private final transient TableModelExampleModel model = new TableModelExampleModel();

	public TableModelExampleView() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		final DynamicView listDynamicView = new DynamicView();
		model.reverseOrder.bind(listDynamicView.reverseOrder());
		model.enableFilter.bind(listDynamicView.enableFilter());

		final ListModel<TestObject> listModel = new RootListModel<>(ListViews.sorted(NATURAL_ORDER));
		final ListModel<TestObject> filteredModel = new ChildListModel<>(listModel, listDynamicView);

		// We could use a separate filter:
		// > final BoundFilter<TestObject, Boolean> filter = BoundFilter.filter((value,
		// > filtered) -> !filtered || value.aSecondValue % 2 == 0);
		// > final ListModel<TestObject> filteredModel = new ChildListModel<>(model,
		// > filtered(filter));
		final TestObjectTableModel tableModel = new TestObjectTableModel(filteredModel);

		final JTable jtable = new JTable(tableModel);

		// The first row will fills the remaining space up to 100% of the width, the
		// second one will have a fixed width of 50px
		final ContributionTableColumnModel<TestObjectTableModel.Columns> columnModel = new ContributionTableColumnModel<>(
				jtable);
		columnModel.install();
		columnModel.configureColumn(ContributionTableColumn.gapColumn(TestObjectTableModel.Columns.A_FIRST_VALUE, 100,
				new DefaultTableCellRenderer()));
		columnModel.configureColumn(ContributionTableColumn.fixedColumn(TestObjectTableModel.Columns.A_SECOND_VALUE, 50,
				new DefaultTableCellRenderer()));

		model.objectSelection.bind(selection(jtable, tableModel)).addDependency(detachOnUpdateOf(filteredModel));
		getContentPane().add(jtable, BorderLayout.CENTER);

		// One can also use a converter to change the model's view, or a
		// BoundComparator, too:
		// > controller.reverseOrder.bind(booleanToOrder()).bind(view(model));

		final JPanel optionsPanel = new JPanel(new FlowLayout());

		final JCheckBox reverse = new JCheckBox("Rev. Order");
		model.reverseOrder.bind(selected(reverse));
		optionsPanel.add(reverse);

		final JCheckBox filter = new JCheckBox("Filter");
		model.enableFilter.bind(selected(filter));
		optionsPanel.add(filter);

		getContentPane().add(optionsPanel, BorderLayout.SOUTH);

		model.setCreated();

		listModel.insert(new TestObject("One", 1));
		listModel.insert(new TestObject("Two", 2));
		listModel.insert(new TestObject("Three", 3));
		listModel.insert(new TestObject("Four", 4));

		validate();
		pack();
	}

}
