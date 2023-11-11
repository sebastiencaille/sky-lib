package ch.scaille.gui.swing.bindings;

import java.util.Collection;
import java.util.function.Supplier;

import javax.swing.JTable;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.gui.swing.factories.SwingBindings;
import ch.scaille.gui.swing.model.ListModelTableModel;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * Binds to multiple selection of JTable.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class JTableMultiSelectionBinding<T, U extends Collection<T>> extends ComponentBindingAdapter<U> {

	private final JTable table;
	private final ListModelTableModel<T, ?> model;
	private boolean modelChange = false;
	private final Supplier<U> collectionType;

	public JTableMultiSelectionBinding(final JTable component, final ListModelTableModel<T, ?> model,
			Supplier<U> collectionType) {
		this.table = component;
		this.model = model;
		this.collectionType = collectionType;
		table.setModel(model);
	}

	private void updateSelection(final IComponentLink<U> componentlink) {
		final var selected = collectionType.get();

		for (final var row : table.getSelectedRows()) {
			if (row >= 0 && row < model.getRowCount()) {
				selected.add(model.getObjectAtRow(row));
			}
		}
		componentlink.setValueFromComponent(table, selected);
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<U> componentlink) {
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && !modelChange) {
				updateSelection(componentlink);
			}
		});
		model.addTableModelListener(event -> {
			if (event.getType() == ListModelTableModel.TABLE_CHANGE_DONE) {
				modelChange = false;
				componentlink.reloadComponentValue();
			} else if (event.getType() == ListModelTableModel.TABLE_ABOUT_TO_CHANGE) {
				modelChange = true;
			}
		});

	}

	@Override
	public void setComponentValue(final AbstractProperty source, final U values) {
		if ((source == null || !source.isModifiedBy(table)) && values != null) {

			table.getSelectionModel().setValueIsAdjusting(true);
			table.getSelectionModel().clearSelection();

			for (final var value : values) {
				final var index = model.getRowOf(value);
				if (index >= 0) {
					table.getSelectionModel().addSelectionInterval(index, index);
				}
			}
			table.getSelectionModel().setValueIsAdjusting(false);
		}
	}

	@Override
	public String toString() {
		return "Multi-selection of " + SwingBindings.nameOf(table);
	}
}
