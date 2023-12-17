package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.Factories.Reporting.checkingValue;
import static ch.scaille.tcwriter.pilot.Factories.Reporting.settingValue;

import javax.swing.JTable;

import org.junit.jupiter.api.Assertions;

@SuppressWarnings("java:S5960")
public class JTablePilot extends AbstractSwingComponent<JTablePilot, JTable> {

	public JTablePilot(final SwingPilot pilot, final String name) {
		super(pilot, JTable.class, name);
	}

	public void selectRow(final int index) {
		waitOn(action(t -> t.setRowSelectionInterval(index, index)).withReportText("select row " + index));
	}

	public void editValue(final int row, final int column, final String value) {
		waitOn(action(t -> t.setValueAt(value, row, column))
				.withReportText(settingValue("at row/column " + row + '/' + column, value)));
	}

	public void editValueOnSelectedRow(final int column, final String value) {
		waitOn(action(t -> {
			t.setValueAt(value, t.getSelectedRow(), column);
			SwingHelper.doPressReturn(t);
		}).withReportText(settingValue("at selected row, column " + column, value)));
	}

	public void checkValue(final int row, final int column, final String expected) {
		waitOn(assertion(pc -> Assertions.assertEquals(expected, pc.component.getValueAt(row, column), pc.description))
				.withReportText(checkingValue("at row/column " + row + '/' + column, expected)));
	}

	public void checkValueOnSelectedRow(final int column, final String expected) {
		waitOn(assertion(pc -> Assertions.assertEquals(expected,
				pc.component.getValueAt(pc.component.getSelectedRow(), column), pc.description))
						.withReportText(checkingValue("at selected row, column " + column, expected)));
	}

}
