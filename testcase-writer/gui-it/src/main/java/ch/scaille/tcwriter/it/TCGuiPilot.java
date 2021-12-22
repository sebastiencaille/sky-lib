package ch.scaille.tcwriter.it;

import ch.scaille.tcwriter.gui.frame.TCWriterGui;
import ch.scaille.tcwriter.pilot.swing.SwingPilot;

public class TCGuiPilot extends SwingPilot {


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
