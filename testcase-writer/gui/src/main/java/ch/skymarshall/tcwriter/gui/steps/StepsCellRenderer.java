package ch.skymarshall.tcwriter.gui.steps;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class StepsCellRenderer extends DefaultTableCellRenderer {

	private final JPanel noRendering = new JPanel();

	public StepsCellRenderer() {
		super();
		noRendering.setSize(0, 0);
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {

		if ((column <= 1 && !StepsTable.displayBreakPoint(row)) || (row % 2 == 0 && column > 1)) {
			noRendering.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
			return noRendering;
		}

		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}
