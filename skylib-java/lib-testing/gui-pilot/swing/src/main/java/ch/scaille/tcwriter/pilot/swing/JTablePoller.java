package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.factories.Reporting.checkingValue;
import static ch.scaille.tcwriter.pilot.factories.Reporting.settingValue;

import javax.swing.JTable;

import org.junit.jupiter.api.Assertions;

import ch.scaille.tcwriter.pilot.swing.JListPoller.SwingPoller;

@SuppressWarnings("java:S5960")
public class JTablePoller extends SwingPollingBuilder<JTable, JTablePoller, JTablePoller.SwingPoller> {

	public static class SwingPoller extends ch.scaille.tcwriter.pilot.swing.SwingPollingBuilder.SwingPoller<JTable> {

		protected SwingPoller(JTablePoller builder) {
			super(builder);
		}

		public void selectRow(final int index) {
			configure(polling -> polling.withReportText("select row " + index))
					.applied(t -> t.setRowSelectionInterval(index, index));
		}

		public void editValue(final int row, final int column, final String value) {
			configure(polling -> polling.withReportText(settingValue("at row/column " + row + '/' + column, value)))
					.applied(t -> t.setValueAt(value, row, column));
		}

		public void editValueOnSelectedRow(final int column, final String value) {
			configure(polling -> polling.withReportText(settingValue("at selected row, column " + column, value)))
					.applied(t -> {
						t.setValueAt(value, t.getSelectedRow(), column);
						SwingHelper.doPressReturn(t);
					});
		}

		public void assertValue(final int row, final int column, final String expected) {
			configure(polling -> polling.withReportText(checkingValue("at row/column " + row + '/' + column, expected)))
					.asserted(pc -> Assertions.assertEquals(expected, pc.getComponent().getValueAt(row, column),
							pc.getDescription()));
		}

		public void assertValueOnSelectedRow(final int column, final String expected) {
			configure(polling -> polling.withReportText(checkingValue("at selected row, column " + column, expected)))
					.asserted(pc -> Assertions.assertEquals(expected,
							pc.getComponent().getValueAt(pc.getComponent().getSelectedRow(), column),
							pc.getDescription()));
		}
	}

	public JTablePoller(final SwingPilot pilot, final String name) {
		super(new SwingComponentPilot<>(pilot, JTable.class, name));
	}
	
	@Override
	public SwingPoller ifNot() {
		return new SwingPoller(this);
	}
	
	public void selectRow(final int index) {
		fail().ifNot().selectRow(index);
	}

	public void editValue(final int row, final int column, final String value) {
		fail().ifNot().editValue(row, column, value);
	}

	public void editValueOnSelectedRow(final int column, final String value) {
		fail().ifNot().editValueOnSelectedRow(column, value);
	}

	public void assertValue(final int row, final int column, final String expected) {
		fail().ifNot().assertValue(row, column, expected);
	}

	public void assertValueOnSelectedRow(final int column, final String expected) {
		fail().ifNot().assertValueOnSelectedRow(column, expected);
	}

}
