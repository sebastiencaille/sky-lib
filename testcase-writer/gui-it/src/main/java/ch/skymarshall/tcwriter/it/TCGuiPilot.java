package ch.skymarshall.tcwriter.it;

import ch.skymarshall.tcwriter.gui.frame.TCWriterGui;
import ch.skymarshall.tcwriter.pilot.swing.SwingPilot;

public class TCGuiPilot extends SwingPilot {

	public static final String STEPS_TABLE = "StepsTable";
	public static final String ADD_STEP = "AddStep";
	private final TCWriterGui root;

	public TCGuiPilot(final TCWriterGui root) {
		super(root);
		this.root = root;
	}

	@Override
	public void close() {
		root.setVisible(false);
		root.dispose();
		super.close();
	}

}
