package ch.scaille.gui.swing;

import java.awt.Point;
import java.util.Collection;

import javax.swing.JTable;

import ch.scaille.gui.swing.model.ListModelTableModel;
import ch.scaille.javabeans.properties.ObjectProperty;

/**
 * Popup on JTable with a ListModelTableModel
 * <p>
 * Provides object at the line the popup was opened.
 * 
 * @author Sebastien Caille
 * 
 * @param <T>
 */
public abstract class AbstractJTablePopup<T> extends AbstractPopup<T> {

	private final JTable table;
	private final ListModelTableModel<T, ?> model;
	private final ObjectProperty<? extends Collection<T>> selections;

	protected AbstractJTablePopup(final JTable table, final ListModelTableModel<T, ?> model,
			final ObjectProperty<T> lastSelected, final ObjectProperty<? extends Collection<T>> selections) {
		super(lastSelected);
		this.table = table;
		this.model = model;
		this.selections = selections;
	}

	@Override
	protected T getValueForPopup(final Point p) {
		final var objectToSelect = model.getObjectAtRow(table.rowAtPoint(p));
		if (objectToSelect != null) {
			var selected = selections != null && selections.getValue() != null
					&& selections.getValue().contains(objectToSelect);
			selected |= lastSelected != null && lastSelected.getValue() == objectToSelect;

			if (!selected) {
				final var index = model.getRowOf(objectToSelect);
				table.getSelectionModel().setSelectionInterval(index, index);
			}
		}
		return objectToSelect;
	}

}
