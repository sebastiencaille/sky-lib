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
package org.skymarshall.hmi.swing17.renderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class SpeedRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		float val = ((Number) value).floatValue();

		if (isSelected) {
			setBackground(table.getSelectionBackground());
		} else if (val > 0.0) {
			setBackground(Color.CYAN.brighter());
		} else {
			setBackground(table.getBackground());
		}

		String unit = "";
		if (val > 1024) {
			unit = "Ko/s";
			val = val / 1024;
		}
		if (val > 1024) {
			unit = "Mo/s";
			val = val / 1024;
		}
		if (val > 1024) {
			unit = "Go/s";
			val = val / 1024;
		}
		String strVal = Float.toString(val);
		final int dotIndex = strVal.indexOf('.');
		if (strVal.length() > dotIndex + 2) {
			strVal = strVal.substring(0, dotIndex + 2);
		}
		return super.getTableCellRendererComponent(table, strVal + unit, isSelected, hasFocus, row, column);
	}

}
