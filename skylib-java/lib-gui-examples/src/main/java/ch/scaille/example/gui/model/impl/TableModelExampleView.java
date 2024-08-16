package ch.scaille.example.gui.model.impl;

import static ch.scaille.gui.swing.factories.SwingBindings.selected;
import static ch.scaille.gui.swing.factories.SwingBindings.selection;
import static ch.scaille.gui.swing.factories.BindingDependencies.preserveOnUpdateOf;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Comparator;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.scaille.example.gui.TestObject;
import ch.scaille.example.gui.TestObjectTableModel;
import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.swing.jtable.PolicyTableColumnModel;
import ch.scaille.gui.swing.jtable.TableColumnWithPolicy;

public class TableModelExampleView extends JFrame {

	static final Comparator<TestObject> NATURAL_ORDER = Comparator.comparingInt(TestObject::getASecondValue);
	static final Comparator<TestObject> REVERSE_ORDER = (o1, o2) -> o2.getASecondValue() - o1.getASecondValue();

    public TableModelExampleView() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		final var listDynamicView = new DynamicView();
        final var  model = new TableModelExampleModel();

        model.reverseOrder.bind(listDynamicView.reverseOrder());
		model.enableFilter.bind(listDynamicView.enableFilter());

		final var listModel = new ListModel<>(ListViews.sorted(NATURAL_ORDER));
		final var filteredModel = listModel.child(listDynamicView);

		// We could use a separate filter:
		// > final BoundFilter<TestObject, Boolean> filter = BoundFilter.filter((value,
		// > filtered) -> !filtered || value.aSecondValue % 2 == 0);
		// > final ListModel<TestObject> filteredModel = new ChildListModel<>(model,
		// > filtered(filter));
		final var tableModel = new TestObjectTableModel(filteredModel);

		final var listTable = new JTable(tableModel);
		listTable.setName("listTable");
		listTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		// The first row will fill the remaining space up to 100% of the width, the
		// second one will have a fixed width of 50px
		final var columnModel = new PolicyTableColumnModel<TestObjectTableModel.Columns>(
				listTable);
		columnModel.install();
		columnModel.configureColumn(
				TableColumnWithPolicy.percentOfAvailableSpace(TestObjectTableModel.Columns.A_FIRST_VALUE, 100)
						.with(new DefaultTableCellRenderer()));
		columnModel
				.configureColumn(TableColumnWithPolicy
						.fixedTextLength(TestObjectTableModel.Columns.A_SECOND_VALUE, 1,
								TableColumnWithPolicy.SAMPLE_NUMBERS, TableColumnWithPolicy.DEFAULT_MARGIN)
						.with(new DefaultTableCellRenderer()));

		model.objectSelection.bind(selection(listTable, tableModel)).addDependency(preserveOnUpdateOf(filteredModel));
		getContentPane().add(listTable, BorderLayout.CENTER);

		// It's also possible to use a converter to change the model's view, or a
		// BoundComparator:
		// > controller.reverseOrder.bind(booleanToOrder()).bind(view(model));

		final var optionsPanel = new JPanel(new FlowLayout());

		final var reverseBtn = new JCheckBox("Rev. Order");
		reverseBtn.setName("reverseOrder");
		model.reverseOrder.bind(selected(reverseBtn));
		optionsPanel.add(reverseBtn);

		final var filterBtn = new JCheckBox("Filter");
		filterBtn.setName("enableFilter");
		model.enableFilter.bind(selected(filterBtn));
		optionsPanel.add(filterBtn);

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
