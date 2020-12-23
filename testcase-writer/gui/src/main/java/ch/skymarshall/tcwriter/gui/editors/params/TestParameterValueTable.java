package ch.skymarshall.tcwriter.gui.editors.params;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import ch.skymarshall.gui.swing.jtable.PolicyTableColumnModel;
import ch.skymarshall.gui.swing.jtable.TableColumnWithPolicy;

public class TestParameterValueTable extends JTable {

	public TestParameterValueTable(final TestParameterValueTableModel testParameterValueTableModel) {
		super(testParameterValueTableModel);
		final PolicyTableColumnModel<TestParameterValueTableModel.Columns> myColumnModel = new PolicyTableColumnModel<>(
				this);
		myColumnModel.install();
		myColumnModel
				.configureColumn(TableColumnWithPolicy.fixedWidth(TestParameterValueTableModel.Columns.MANDATORY, 50)
						.apply(getDefaultRenderer(Boolean.class)));
		myColumnModel.configureColumn(TableColumnWithPolicy.fixedWidth(TestParameterValueTableModel.Columns.ENABLED, 50)
				.apply(getDefaultRenderer(Boolean.class)));
		myColumnModel
				.configureColumn(TableColumnWithPolicy.fixedWidth(TestParameterValueTableModel.Columns.DESCRIPTION, 200)
						.apply(new DefaultTableCellRenderer()));
		myColumnModel.configureColumn(
				TableColumnWithPolicy.percentOfAvailableSpace(TestParameterValueTableModel.Columns.VALUE, 100));
	}

	@Override
	public TableCellEditor getCellEditor(final int row, final int column) {
		if (column == 1) {
			return getDefaultEditor(Boolean.class);
		}
		return super.getCellEditor(row, column);
	}

}
