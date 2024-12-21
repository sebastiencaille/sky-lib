package ch.scaille.gui.swing.bindings;

import javax.swing.JTable;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.gui.swing.model.ListModelTableModel;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;

public class JTableSelectionBinding<T> extends ComponentBindingAdapter<T> {

	private final JTable table;
	private final ListModelTableModel<T, ?> model;

	private boolean modelChange = false;
	private IComponentLink<T> converter;

	public JTableSelectionBinding(final JTable component, final ListModelTableModel<T, ?> model) {
		this.table = component;
		this.model = model;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<T> converter) {
		this.converter = converter;
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && !modelChange) {
				updateSelection(converter);
			}
		});

		model.addTableModelListener(event -> {
			if (event.getType() == ListModelTableModel.TABLE_CHANGE_DONE) {
				modelChange = false;
				converter.reloadComponentValue();
			} else if (event.getType() == ListModelTableModel.TABLE_ABOUT_TO_CHANGE) {
				modelChange = true;
			}
		});

	}

	@Override
	public void setComponentValue(final IComponentChangeSource source, final T value) {
		if (source == null || !source.isModifiedBy(table)) {
			if (value == null) {
				table.getSelectionModel().clearSelection();
			} else {
				final var index = model.getRowOf(value);
				if (table.getSelectedRow() != index && index >= 0) {
					table.getSelectionModel().setSelectionInterval(index, index);
				}
				if (index < 0) {
					converter.setValueFromComponent(this, null);
				}
			}
		}
	}

	protected void updateSelection(final IComponentLink<T> converter) {
		final var selectedRow = table.getSelectedRow();
		if (selectedRow >= 0) {
			final var object = model.getObjectAtRow(selectedRow);
			converter.setValueFromComponent(table, object);
		} else {
			converter.setValueFromComponent(table, null);
		}
	}

	@Override
	public String toString() {
		return "Selection of " + table;
	}

}
