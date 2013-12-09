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

import javax.swing.event.EventListenerList;

import org.skymarshall.hmi.mvc.properties.AbstractProperty;

/**
 * Allows firering {@link IPropertyEventListener} when the value of a property
 * in this group is changed
 * 
 * @author Sebastien Caille
 * 
 */
public class PropertyGroup {

    private final EventListenerList actions   = new EventListenerList();

    private int                     callCount = 0;

    private class Impl implements
            IPropertyEventListener {
        @Override
        public void propertyModified(final Object caller, final PropertyEvent event) {
            switch (event.getKind()) {
            case BEFORE:
                if (callCount > 0) {
                    return;
                }
                callCount++;
                break;

            case AFTER:
                callCount--;
                if (callCount != 0) {
                    return;
                }
                break;
            }
            for (final IPropertyEventListener action : actions.getListeners(IPropertyEventListener.class)) {
                action.propertyModified(caller, event);
            }
        }
    }

    private final Impl impl = new Impl();

    public void addProperty(final AbstractProperty prop) {
        prop.addListener(impl);
    }

    public void addAction(final IPropertyEventListener action) {
        actions.add(IPropertyEventListener.class, action);
    }

}
