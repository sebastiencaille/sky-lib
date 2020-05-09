package ch.skymarshall.tcwriter.pilot.swing;

import static ch.skymarshall.tcwriter.pilot.Polling.action;

import javax.swing.JTable;

public class SwingTable extends AbstractSwingComponent<JTable> {

	public SwingTable(final GuiPilot pilot, final String name) {
		super(pilot, JTable.class, name);
	}

	public void selectRow(final int index) {
		withReport(c -> "select row at " + index)
				.waitEditSuccess(action(t -> t.setRowSelectionInterval(index, index)));
	}

}
