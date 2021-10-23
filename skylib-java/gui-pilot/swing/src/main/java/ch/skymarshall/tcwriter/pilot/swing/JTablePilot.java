package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JTable;

import org.junit.jupiter.api.Assertions;

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
		}).withReport(c -> "edit value of selected row, column " + column));
	}

	public void checkValue(final int row, final int column, final String expected) {
		wait(assertEquals("check value at " + row + '/' + column, expected, t-> t.getValueAt(row, column)));
	}

	public void checkValueOnSelectedRow(final int column, final String expected) {
		wait(assertEquals("check value of selected row, column "
				+ column, expected, t -> t.getValueAt(t.getSelectedRow(), column)));
	}

}
