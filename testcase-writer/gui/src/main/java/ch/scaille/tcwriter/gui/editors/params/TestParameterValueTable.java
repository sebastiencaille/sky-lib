package ch.scaille.tcwriter.gui.editors.params;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import ch.scaille.gui.swing.jtable.PolicyTableColumnModel;
import ch.scaille.gui.swing.jtable.TableColumnWithPolicy;

public class TestParameterValueTable extends JTable {

	public TestParameterValueTable(final TestParameterValueTableModel testParameterValueTableModel) {
		super(testParameterValueTableModel);
		final var myColumnModel = new PolicyTableColumnModel<TestParameterValueTableModel.Columns>(this);
		myColumnModel.install();
		myColumnModel
				.configureColumn(TableColumnWithPolicy.fixedWidth(TestParameterValueTableModel.Columns.MANDATORY, 50)
						.with(getDefaultRenderer(Boolean.class)));
		myColumnModel.configureColumn(TableColumnWithPolicy.fixedWidth(TestParameterValueTableModel.Columns.ENABLED, 50)
				.with(getDefaultRenderer(Boolean.class)));
		myColumnModel
				.configureColumn(TableColumnWithPolicy.fixedWidth(TestParameterValueTableModel.Columns.DESCRIPTION, 200)
						.with(new DefaultTableCellRenderer()));
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
