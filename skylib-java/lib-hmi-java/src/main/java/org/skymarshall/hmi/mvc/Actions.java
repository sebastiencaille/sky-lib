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
package org.skymarshall.hmi.mvc;

import org.skymarshall.hmi.model.IListModelListener;
import org.skymarshall.hmi.model.ListEvent;
import org.skymarshall.hmi.mvc.PropertyEvent.EventKind;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

/**
 * Some default actions performed when property events are fired.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 */
public class Actions {

    /**
     * Restores the selection once some properties are fired.
     * <p>
     * 
     * @param property
     *            the property that contains the selection to restore
     * @return an action
     */
    public static IPropertyEventListener restoreAfterUpdate(final AbstractProperty property) {
        return new IPropertyEventListener() {

            @Override
            public void propertyModified(final Object caller, final PropertyEvent event) {
                if (event.getKind() == EventKind.BEFORE) {
                    property.detach();
                } else if (event.getKind() == EventKind.AFTER) {
                    property.attach();
                }
            }
        };
    }

    /**
     * Restores the selection once some properties are fired.
     * <p>
     * 
     * @param property
     *            the property that contains the selection to restore
     * @return an action
     */
    public static <T> IListModelListener<T> restoreAfterListModelUpdate(final AbstractProperty property) {
        return new IListModelListener<T>() {

            @Override
            public void mutates() {
                property.detach();
            }

            @Override
            public void valuesSet(final ListEvent<T> event) {
                property.attach();
            }

            @Override
            public void valuesCleared(final ListEvent<T> event) {
                property.attach();
            }

            @Override
            public void valuesAdded(final ListEvent<T> event) {
                property.attach();
            }

            @Override
            public void valuesRemoved(final ListEvent<T> event) {
                property.attach();
            }

            @Override
            public void editionCancelled(final ListEvent<T> event) {
                // noop
            }

            @Override
            public void editionsStarted(final ListEvent<T> event) {
                // noop
            }

            @Override
            public void editionsStopping(final ListEvent<T> event) {
                // noop
            }

            @Override
            public void editionsStopped(final ListEvent<T> event) {
                // noop
            }

        };
    }
}
