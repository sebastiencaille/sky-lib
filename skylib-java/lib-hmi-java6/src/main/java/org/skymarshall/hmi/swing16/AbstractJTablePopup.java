/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.swing16;

import java.awt.Point;
import java.util.Collection;

import javax.swing.JTable;

import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.swing16.model.ListModelTableModel;

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

    private final JTable                                  table;
    private final ListModelTableModel<T, ?>               model;
    private final ObjectProperty<? extends Collection<T>> selections;

    public AbstractJTablePopup(final JTable table, final ListModelTableModel<T, ?> model,
            final ObjectProperty<T> lastSelected, final ObjectProperty<? extends Collection<T>> selections) {
        super(lastSelected);
        this.table = table;
        this.model = model;
        this.selections = selections;
    }

    @Override
    protected T getValueForPopup(final Point p) {
        final T objectToSelect = model.getObjectAtRow(table.rowAtPoint(p));
        if (objectToSelect != null) {
            boolean selected = selections != null && selections.getValue() != null
                    && selections.getValue().contains(objectToSelect);
            selected |= lastSelected != null && lastSelected.getValue() == objectToSelect;

            if (!selected) {
                final int index = model.getRowOf(objectToSelect);
                table.getSelectionModel().setSelectionInterval(index, index);
            }
        }
        return objectToSelect;
    }

}
