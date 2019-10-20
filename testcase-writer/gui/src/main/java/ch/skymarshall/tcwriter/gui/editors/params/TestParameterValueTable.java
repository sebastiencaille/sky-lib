package ch.skymarshall.tcwriter.gui.editors.params;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ch.skymarshall.gui.swing.ContributionTableColumn;
import ch.skymarshall.gui.swing.ContributionTableColumnModel;

public class TestParameterValueTable extends JTable {

	private final TestParameterValueTableModel testParameterValueTableModel;

	public TestParameterValueTable(final TestParameterValueTableModel testParameterValueTableModel) {
		super(testParameterValueTableModel);
		this.testParameterValueTableModel = testParameterValueTableModel;
		final ContributionTableColumnModel<TestParameterValueTableModel.Columns> columnModel = new ContributionTableColumnModel<>(
				this);
		columnModel.install();
		columnModel.configureColumn(ContributionTableColumn.fixedColumn(TestParameterValueTableModel.Columns.ENABLED,
				50, new DefaultTableCellRenderer()));
		columnModel.configureColumn(ContributionTableColumn
				.fixedColumn(TestParameterValueTableModel.Columns.DESCRIPTION, 200, new DefaultTableCellRenderer()));
		columnModel.configureColumn(ContributionTableColumn.gapColumn(TestParameterValueTableModel.Columns.VALUE, 100,
				new DefaultTableCellRenderer()));
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return (column == 0 && !testParameterValueTableModel.getObjectAtRow(row).mandatory) || column == 2;
	}

	@Override
	public TableCellRenderer getCellRenderer(final int row, final int column) {
		if (column == 0) {
			return getDefaultRenderer(Boolean.class);
		}
		return super.getCellRenderer(row, column);
	}

	@Override
	public TableCellEditor getCellEditor(final int row, final int column) {
		if (column == 0) {
			return getDefaultEditor(Boolean.class);
		}
		return super.getCellEditor(row, column);
	}

}
