package ch.scaille.tcwriter.gui.steps;

import java.awt.Graphics;

import javax.swing.table.DefaultTableCellRenderer;

public class StepsCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 2432608849372447660L;

	public StepsCellRenderer() {
		super();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		final var clip = g.getClip().getBounds();
		setSize((int) clip.getWidth(), (int) (clip.getHeight() / 2));
		super.paintComponent(g.create(0, clip.height / 2 + 1, clip.width, clip.height / 2));
	}

}
