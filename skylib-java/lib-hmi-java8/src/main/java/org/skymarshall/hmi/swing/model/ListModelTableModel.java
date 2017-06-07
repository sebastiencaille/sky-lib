/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.swing.model;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.skymarshall.hmi.model.ListModel;

/**
 * Table model based on a {@link ListModel}, with enum based column identifiers.
 * <p>
 * The order of the columns is based on the ordinal of the enum components.
 * 
 * @author Sebastien Caille
 * 
 * @param <T>
 *            type of data contained in the model
 * @param <C>
 *            enum of columns describing the model
 */
public abstract class ListModelTableModel<T, C extends Enum<C>> extends AbstractTableModel implements
        ListDataListener {

    private static final long    serialVersionUID      = -877625721248108739L;

    public static final int      TABLE_ABOUT_TO_CHANGE = 98;
    public static final int      TABLE_CHANGE_DONE     = 99;

    protected final ListModel<T> model;
    private final Class<C>       columnsEnumClass;
    private final Enum<C>[]      columnsEnum;

    protected int                warningLimit          = 20;

    protected abstract Object getValueAtColumn(T object, C column);

    protected abstract void setValueAtColumn(T object, C column, Object value);

    public ListModelTableModel(final ListModel<T> model, final Class<C> columnsEnumClass) {
        this.model = model;
        this.columnsEnumClass = columnsEnumClass;
        this.columnsEnum = columnsEnumClass.getEnumConstants();
        model.addListDataListener(this);
    }

    public ListModel<T> getBaseModel() {
        return model;
    }

    protected void setWarningLimit(final int warningLimit) {
        this.warningLimit = warningLimit;
    }

    protected void warn(final String string) {
        System.out.println("WARNING: " + string);
    }

    @Override
    public int getColumnCount() {
        return columnsEnum.length;
    }

    @Override
    public int getRowCount() {
        return model.getSize();
    }

    public int getIndexOf(final C column) {
        return column.ordinal();
    }

    @Override
    public Object getValueAt(final int row, final int column) {
        return getValueAtColumn(model.getValueAt(row), columnsEnumClass.cast(columnsEnum[column]));
    }

    @Override
    public void setValueAt(final Object aValue, final int row, final int column) {
        super.setValueAt(aValue, row, column);
        final T editedValue = model.getValueAt(row);
        model.startEditingValue(editedValue);
        try {
            setValueAtColumn(model.getValueAt(row), columnsEnumClass.cast(columnsEnum[column]), aValue);
        } finally {
            model.stopEditingValue();
        }
    }

    @Override
    public void contentsChanged(final ListDataEvent event) {
        fireTableRowsUpdated(event.getIndex0(), event.getIndex1());
    }

    @Override
    public void intervalAdded(final ListDataEvent event) {
        fireTableChanged(new TableModelEvent(this, event.getIndex0(), event.getIndex1(), TableModelEvent.ALL_COLUMNS,
                TABLE_ABOUT_TO_CHANGE));
        fireTableRowsInserted(event.getIndex0(), event.getIndex1());
        fireTableChanged(new TableModelEvent(this, event.getIndex0(), event.getIndex1(), TableModelEvent.ALL_COLUMNS,
                TABLE_CHANGE_DONE));
    }

    @Override
    public void intervalRemoved(final ListDataEvent event) {
        // if (verbose()) {
        // log("Deleted range: " + event.getIndex0() + "/" + event.getIndex1());
        //
        // fireTableChanged(new TableModelEvent(this, event.getIndex0(),
        // event.getIndex1(),
        // TableModelEvent.ALL_COLUMNS, TABLE_ABOUT_TO_CHANGE));
        // }
        fireTableRowsDeleted(event.getIndex0(), event.getIndex1());
        fireTableChanged(new TableModelEvent(this, event.getIndex0(), event.getIndex1(), TableModelEvent.ALL_COLUMNS,
                TABLE_CHANGE_DONE));
    }

    public T getObjectAtRow(final int row) {
        return model.getValueAt(row);
    }

    public int getRowOf(final T object) {
        return model.getRowOf(object);
    }

    @Override
    public String getColumnName(final int column) {
        return columnsEnum[column].name();
    }

    protected boolean verbose() {
        return false;
    }

}
