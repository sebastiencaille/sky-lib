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
package org.skymarshall.hmi.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import org.skymarshall.hmi.swing.model.ListModelTableModel;

/**
 * Column model that uses {@link ContributionTableColumn}.
 * <p>
 * The remaining space will be occupied by gapColumns.
 *
 * @author Sebastien Caille
 *
 * @param <C>
 *            enum that defines the columns (see {@link ListModelTableModel}
 */
public class ContributionTableColumnModel<C extends Enum<C>> extends DefaultTableColumnModel {

    private final JTable table;

    public ContributionTableColumnModel(final JTable table) {
        this.table = table;
        table.addPropertyChangeListener("columnModel", new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent paramPropertyChangeEvent) {
                table.setAutoCreateColumnsFromModel(false);
            }
        });
    }

    public ContributionTableColumnModel<C> install() {
        table.setColumnModel(this);
        return this;
    }

    /**
     * Updates the width of the columns
     */
    void update() {

        final ColumnContribution contribution = new ColumnContribution();

        // Gets the contribution of known columns width
        final Enumeration<TableColumn> columns = getColumns();
        while (columns.hasMoreElements()) {
            final TableColumn col = columns.nextElement();
            if (col instanceof ContributionTableColumn) {
                final ContributionTableColumn<?> ctc = (ContributionTableColumn<?>) col;
                ctc.contribute(contribution);
            } else {
                contribution.allocatedWidth += col.getWidth();
            }
        }
        if (contribution.gapColumnCount == 0) {
            // provide a phantom gap column to make our life easier
            contribution.gapColumnCount = 1;
        }
        // Computes the actual size of the columns
        final int width = table.getWidth();
        contribution.unallocateWidth = width - contribution.allocatedWidth;
        // getWidth may have re-created the columns
        final Enumeration<TableColumn> newColumns = getColumns();
        while (newColumns.hasMoreElements()) {
            final TableColumn col = newColumns.nextElement();
            if (col instanceof ContributionTableColumn) {
                ((ContributionTableColumn<?>) col).computeWidth(contribution);
            }
        }
        totalColumnWidth = -1;
    }

    public void configureColumn(final ContributionTableColumn<C> column) {
        if (table.getColumnModel() != this) {
            throw new IllegalStateException("Table column model is not this model");
        }
        if (tableColumns.isEmpty()) {
            table.createDefaultColumnsFromModel();
        }
        final int index = JTableHelper.columnIndex(table, column.getColumn());
        column.setModelIndex(index);
        final TableColumn old = tableColumns.get(index);
        tableColumns.removeElementAt(index);
        column.setHeaderRenderer(old.getHeaderRenderer());
        column.setHeaderValue(old.getHeaderValue());
        column.setModel(this);
        tableColumns.add(index, column);
        update();
    }

    @Override
    protected void recalcWidthCache() {
        update();
        super.recalcWidthCache();
    }

}
