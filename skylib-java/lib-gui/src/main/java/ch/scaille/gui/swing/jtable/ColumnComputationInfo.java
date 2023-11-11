package ch.scaille.gui.swing.jtable;

import javax.swing.JTable;

class ColumnComputationInfo {

	final int sameClassCount;
	final int tableWidth;
	final int unallocatedWidth;
	final JTable table;

	public ColumnComputationInfo(final JTable table, final int sameClassCount, final int unallocatedWidth) {
		super();
		this.table = table;
		this.sameClassCount = sameClassCount;
		this.tableWidth = table.getWidth();
		this.unallocatedWidth = unallocatedWidth;
	}

	public Object getFont() {
		return table.getFont();
	}
	
}
