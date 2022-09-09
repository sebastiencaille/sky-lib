/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.scaille.gui.swing.jtable;

import java.awt.FontMetrics;
import java.awt.Toolkit;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

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
	
	public String getFontDesciption() {
		return table.getFont().toString();
	}
	
	public int computeWidth(String text) {
		FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(table.getFont());
		return SwingUtilities.computeStringWidth(metrics, text);
	}

}
