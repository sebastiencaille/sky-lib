package ch.scaille.gui.swing.bindings;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

import ch.scaille.gui.swing.model.ListModelTableModel;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import lombok.extern.java.Log;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

@Log
public class JTableSelectionBinding<T> implements IComponentBinding<@Nullable T> {

    private final JTable table;
    private final ListModelTableModel<T, ?> model;

    private boolean modelChange = false;
    @Nullable
    private IComponentLink<@Nullable T> converter;

	public JTableSelectionBinding(final JTable component, final ListModelTableModel<T, ?> model) {
        this.table = component;
        this.model = model;
    }

    @Override
    public void addComponentValueChangeListener(final IComponentLink<T> converter) {
        this.converter = converter;
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && !modelChange) {
                updateSelection(converter);
            }
        });

        model.addTableModelListener(event -> {
        	switch (event.getType()) {
        	case ListModelTableModel.TABLE_CHANGE_DONE:
                modelChange = false;
                converter.reloadComponentValue();
                break;
        	case ListModelTableModel.TABLE_ABOUT_TO_CHANGE:
                modelChange = true;
        		break;
        	case TableModelEvent.UPDATE:
      			updateSelection(converter);
            	break;
            default:
                break;
            }
        });

    }

    @Override
    public void setComponentValue(final IComponentChangeSource source, @Nullable final T value) {
        if (source.isModifiedBy(table)) {
            return;
        }
        if (value == null) {
            table.getSelectionModel().clearSelection();
        } else {
            final var index = model.getRowOf(value);
            if (table.getSelectedRow() != index && index >= 0) {
                table.getSelectionModel().setSelectionInterval(index, index);
            }
            if (index < 0) {
                converterSafe().setValueFromComponent(this, null);
            }
        }
    }

    protected void updateSelection(final IComponentLink<@Nullable T> converter) {
        final var selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            final var object = model.getObjectAtRow(selectedRow);
            converter.setValueFromComponent(table, object, true);
        } else {
            converter.setValueFromComponent(table, null);
        }
    }

    private IComponentLink<@Nullable T> converterSafe() {
        return Objects.requireNonNull(converter, "Component value change listener not set yet");
    }

    @Override
    public String toString() {
        return "Selection of " + table;
    }

}
