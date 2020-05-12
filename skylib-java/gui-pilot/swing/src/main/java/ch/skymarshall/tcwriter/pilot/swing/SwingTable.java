package ch.skymarshall.tcwriter.pilot.swing;

import static ch.skymarshall.tcwriter.pilot.Polling.action;

import javax.swing.JTable;

import org.junit.Assert;

import ch.skymarshall.tcwriter.pilot.Polling;

public class SwingTable extends AbstractSwingComponent<JTable> {

	public SwingTable(final SwingGuiPilot pilot, final String name) {
		super(pilot, JTable.class, name);
	}

	public void selectRow(final int index) {
		withReport(c -> "select row at " + index).waitEditSuccess(action(t -> t.setRowSelectionInterval(index, index)));
	}

	public void editValue(final int row, final int column, final String value) {
		withReport(c -> "edit value '" + value + "' at " + row + '/' + column)
				.waitEditSuccess(action(t -> t.setValueAt(value, row, column)));
	}

	public void checkValue(final int row, final int column, final String value) {
		withReport(c -> "check value '" + value + "' at " + row + '/' + column)
				.waitStateSuccess(Polling.assertion(t -> Assert.assertEquals(value, t.getValueAt(row, column))));
	}

	public void editValueOnSelectedRow(final int column, final String value) {
		withReport(c -> "edit value '" + value + "' of selected row, column " + column).waitEditSuccess(action(t -> {
			t.setValueAt(value, t.getSelectedRow(), column);
			pressReturn(t);
		}));
	}

	public void checkValueOnSelectedRow(final int column, final String value) {
		withReport(c -> "check value '" + value + "' of selected row, column " + column).waitStateSuccess(
				Polling.assertion(t -> Assert.assertEquals(value, t.getValueAt(t.getSelectedRow(), column))));
	}

}
