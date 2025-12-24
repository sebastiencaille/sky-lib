package ch.scaille.tcwriter.gui.editors.params;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import ch.scaille.gui.swing.jtable.PolicyTableColumnModel;
import ch.scaille.gui.swing.jtable.TableColumnWithPolicy;

import java.io.Serial;

import static ch.scaille.gui.swing.jtable.TableColumnWithPolicy.fixedTextLength;

public class TestParameterValueTable extends JTable {

    @Serial
    private static final long serialVersionUID = -6414986043651145474L;

    public TestParameterValueTable(final TestParameterValueTableModel testParameterValueTableModel) {
        super(testParameterValueTableModel);
        final var myColumnModel = new PolicyTableColumnModel<TestParameterValueTableModel.Columns>(this);
        myColumnModel.install();
        myColumnModel.configureColumn(fixedTextLength(TestParameterValueTableModel.Columns.MANDATORY, 5)
                        .with(getDefaultRenderer(Boolean.class)));
        myColumnModel.configureColumn(fixedTextLength(TestParameterValueTableModel.Columns.ENABLED, 5)
                .with(getDefaultRenderer(Boolean.class)));
        myColumnModel.configureColumn(fixedTextLength(TestParameterValueTableModel.Columns.DESCRIPTION, 25)
                        .with(new DefaultTableCellRenderer()));
        myColumnModel.configureColumn(TableColumnWithPolicy.percentOfAvailableSpace(TestParameterValueTableModel.Columns.VALUE, 100));
    }

    @Override
    public TableCellEditor getCellEditor(final int row, final int column) {
        if (column == 1) {
            return getDefaultEditor(Boolean.class);
        }
        return super.getCellEditor(row, column);
    }

}
