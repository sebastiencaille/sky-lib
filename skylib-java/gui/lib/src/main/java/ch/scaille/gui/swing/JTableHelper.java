package ch.scaille.gui.swing;

import java.awt.Point;

import javax.swing.JTable;

import ch.scaille.gui.swing.model.ListModelTableModel;

public interface JTableHelper {

	static <C extends Enum<C>> C columnAt(final JTable table, final Point p, final Class<C> columnClazz) {
		return columnClazz.getEnumConstants()[table.convertColumnIndexToModel(table.columnAtPoint(p))];
	}

	static <C extends Enum<C>> int modelColumnIndex(final JTable table, final C col) {
		return ((ListModelTableModel<?, C>) table.getModel()).getIndexOf(col);
	}

}
