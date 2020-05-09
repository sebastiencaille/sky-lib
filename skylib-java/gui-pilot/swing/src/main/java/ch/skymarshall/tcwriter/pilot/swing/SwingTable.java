package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JTable;

public class SwingTable extends AbstractSwingComponent<JTable> {

	public SwingTable(final GuiPilot pilot, final String name) {
		super(pilot, JTable.class, name);
	}

}
