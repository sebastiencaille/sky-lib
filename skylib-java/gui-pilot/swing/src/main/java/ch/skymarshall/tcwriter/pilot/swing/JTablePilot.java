package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JTable;

import org.junit.Assert;

public class JTablePilot extends AbstractSwingComponent<JTablePilot, JTable> {

	public JTablePilot(final SwingPilot pilot, final String name) {
		super(pilot, JTable.class, name);
	}

	public void selectRow(final int index) {
		wait(action(t -> t.setRowSelectionInterval(index, index)).withReport(c -> "select row at " + index));
	}

	public void editValue(final int row, final int column, final String value) {
		wait(action(t -> t.setValueAt(value, row, column))
				.withReport(c -> "edit value '" + value + "' at " + row + '/' + column));
	}

	public void editValueOnSelectedRow(final int column, final String value) {
		wait(action(t -> {
			t.setValueAt(value, t.getSelectedRow(), column);
			doPressReturn(t);
		}).withReport(c -> "edit value '" + value + "' of selected row, column " + column));
	}

	public void checkValue(final int row, final int column, final String value) {
		wait(assertion(t -> Assert.assertEquals(value, t.getValueAt(row, column)))
				.withReport(c -> "check value '" + value + "' at " + row + '/' + column));
	}

	public void checkValueOnSelectedRow(final int column, final String value) {
		wait(assertion(t -> Assert.assertEquals(value, t.getValueAt(t.getSelectedRow(), column)))
				.withReport(c -> "check value '" + value + "' of selected row, column " + column));
	}

}
