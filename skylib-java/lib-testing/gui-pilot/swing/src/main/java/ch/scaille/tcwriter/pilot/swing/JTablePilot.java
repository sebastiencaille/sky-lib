package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.factories.Reporting.checkingValue;
import static ch.scaille.tcwriter.pilot.factories.Reporting.settingValue;

import javax.swing.JTable;

import org.junit.jupiter.api.Assertions;

@SuppressWarnings("java:S5960")
public class JTablePilot extends AbstractSwingComponentPilot<JTablePilot, JTable> {

	public JTablePilot(final SwingPilot pilot, final String name) {
		super(pilot, JTable.class, name);
	}

	public void selectRow(final int index) {
		polling().fail("select row " + index).ifNot().applied(t -> t.setRowSelectionInterval(index, index));
	}

	public void editValue(final int row, final int column, final String value) {
		polling().fail(settingValue("at row/column " + row + '/' + column, value))
				.ifNot()
				.applied(t -> t.setValueAt(value, row, column));
	}

	public void editValueOnSelectedRow(final int column, final String value) {
		polling().fail(settingValue("at selected row, column " + column, value)).ifNot().applied(t -> {
			t.setValueAt(value, t.getSelectedRow(), column);
			SwingHelper.doPressReturn(t);
		});
	}

	public void assertValue(final int row, final int column, final String expected) {
		polling().fail(checkingValue("at row/column " + row + '/' + column, expected))
				.ifNot()
				.asserted(pc -> Assertions.assertEquals(expected, pc.getComponent().getValueAt(row, column),
						pc.getDescription()));
	}

	public void assertValueOnSelectedRow(final int column, final String expected) {
		polling().fail(checkingValue("at selected row, column " + column, expected))
				.ifNot()
				.asserted(pc -> Assertions.assertEquals(expected,
						pc.getComponent().getValueAt(pc.getComponent().getSelectedRow(), column), pc.getDescription()));

	}

}
