package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.factories.Reporting.checkingValue;
import static ch.scaille.tcwriter.pilot.factories.Reporting.settingValue;

import javax.swing.JTable;

import org.junit.jupiter.api.Assertions;

@SuppressWarnings("java:S5960")
public class JTablePilot extends AbstractSwingComponent<JTablePilot, JTable> {

	public JTablePilot(final SwingPilot pilot, final String name) {
		super(pilot, JTable.class, name);
	}

	public void selectRow(final int index) {
		polling().apply(t -> t.setRowSelectionInterval(index, index)).orFail(("select row " + index));
	}

	public void editValue(final int row, final int column, final String value) {
		polling().apply(t -> t.setValueAt(value, row, column))
				.orFail((settingValue("at row/column " + row + '/' + column, value)));
	}

	public void editValueOnSelectedRow(final int column, final String value) {
		polling().apply(t -> {
			t.setValueAt(value, t.getSelectedRow(), column);
			SwingHelper.doPressReturn(t);
		}).orFail((settingValue("at selected row, column " + column, value)));
	}

	public void assertValue(final int row, final int column, final String expected) {
		polling().asserts(pc -> Assertions.assertEquals(expected, pc.getComponent().getValueAt(row, column), pc.getDescription()))
				.orFail((checkingValue("at row/column " + row + '/' + column, expected)));
	}

	public void assertValueOnSelectedRow(final int column, final String expected) {
		polling().asserts(pc -> Assertions.assertEquals(expected,
				pc.getComponent().getValueAt(pc.getComponent().getSelectedRow(), column), pc.getDescription()))
				.orFail((checkingValue("at selected row, column " + column, expected)));
	}

}
