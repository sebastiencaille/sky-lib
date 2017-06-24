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
package org.skymarshall.hmi.swing;

import java.awt.Point;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.skymarshall.hmi.swing.model.ListModelTableModel;

public class JTableHelper {
	private JTableHelper() {
	}

	public static <C extends Enum<C>> TableColumn getColumn(final JTable table, final C col) {
		return table.getColumnModel().getColumn(((ListModelTableModel<?, C>) table.getModel()).getIndexOf(col));
	}

	public static <C extends Enum<C>> C columnAt(final JTable table, final Point p, final Class<C> columnClazz) {
		return columnClazz.getEnumConstants()[table.columnAtPoint(p)];
	}

	public static <C extends Enum<C>> int columnIndex(final JTable table, final C col) {
		return ((ListModelTableModel<?, C>) table.getModel()).getIndexOf(col);
	}

}
