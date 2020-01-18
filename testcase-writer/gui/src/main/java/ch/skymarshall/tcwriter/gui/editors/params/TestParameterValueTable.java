package ch.skymarshall.tcwriter.gui.editors.params;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import ch.skymarshall.gui.swing.jtable.TableColumnWithPolicy;
import ch.skymarshall.gui.swing.jtable.PolicyTableColumnModel;

public class TestParameterValueTable extends JTable {

	private final TestParameterValueTableModel testParameterValueTableModel;

	public TestParameterValueTable(final TestParameterValueTableModel testParameterValueTableModel) {
		super(testParameterValueTableModel);
		this.testParameterValueTableModel = testParameterValueTableModel;
		final PolicyTableColumnModel<TestParameterValueTableModel.Columns> columnModel = new PolicyTableColumnModel<>(
				this);
		columnModel.install();
		columnModel.configureColumn(TableColumnWithPolicy.fixedWidth(TestParameterValueTableModel.Columns.MANDATORY, 50)
				.apply(getDefaultRenderer(Boolean.class)));
		columnModel.configureColumn(TableColumnWithPolicy.fixedWidth(TestParameterValueTableModel.Columns.ENABLED, 50)
				.apply(getDefaultRenderer(Boolean.class)));
		columnModel.configureColumn(TableColumnWithPolicy.fixedWidth(TestParameterValueTableModel.Columns.DESCRIPTION, 200)
				.apply(new DefaultTableCellRenderer()));
		columnModel
				.configureColumn(TableColumnWithPolicy.percentOfAvailableSpace(TestParameterValueTableModel.Columns.VALUE, 100));
	}

	@Override
	public TableCellEditor getCellEditor(final int row, final int column) {
		if (column == 1) {
			return getDefaultEditor(Boolean.class);
		}
		return super.getCellEditor(row, column);
	}

}
