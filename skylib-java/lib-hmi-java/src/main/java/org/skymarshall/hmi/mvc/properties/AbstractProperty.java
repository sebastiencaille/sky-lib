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
package org.skymarshall.hmi.mvc.properties;

import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.HmiErrors.HmiError;
import org.skymarshall.hmi.mvc.IPropertyEventListener;
import org.skymarshall.hmi.mvc.PropertyEvent;
import org.skymarshall.hmi.mvc.PropertyEvent.EventKind;

/**
 * Provides default type-independant mechanisms of properties.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 */
public abstract class AbstractProperty {

    public static final ErrorNotifier               EMPTY_NOTIFIER = new ErrorNotifier() {
                                                                       @Override
                                                                       public void setError(final Object caller,
                                                                               final HmiError e) {
                                                                           // nope
                                                                       }
                                                                   };

    /**
     * Name of the property
     */
    private final String                            name;

    /**
     * Support to trigger property change
     */
    protected final ControllerPropertyChangeSupport propertySupport;

    /**
     * Property related events (before firing, after firing, ...)
     */
    protected EventListenerList                     eventListeners = new EventListenerList();

    /**
     * Error property
     */
    protected ErrorNotifier                         errorNotifier  = EMPTY_NOTIFIER;

    protected boolean                               attached       = false;

    public abstract void reset(final Object caller);

    public abstract void load(final Object caller);

    public abstract void save();

    public AbstractProperty(final String name, final ControllerPropertyChangeSupport propertySupport) {
        this.name = name;
        this.propertySupport = propertySupport;
        propertySupport.register(this);
    }

    public String getName() {
        return name;
    }

    public void detach() {
        attached = false;
    }

    public void attach() {
        attached = true;
    }

    public boolean isAttached() {
        return attached;
    }

    public void setErrorNotifier(final ErrorNotifier errorNotifier) {
        if (errorNotifier != null) {
            this.errorNotifier = errorNotifier;
        } else {
            this.errorNotifier = EMPTY_NOTIFIER;
        }
    }

    public void addListener(final PropertyChangeListener propertyChangeListener) {
        propertySupport.addPropertyChangeListener(name, propertyChangeListener);
    }

    public void removeListener(final PropertyChangeListener propertyChangeListener) {
        propertySupport.removePropertyChangeListener(name, propertyChangeListener);
    }

    public boolean isModifiedBy(final Object caller) {
        return propertySupport.isModifiedBy(name, caller);
    }

    public void addListener(final IPropertyEventListener listener) {
        eventListeners.add(IPropertyEventListener.class, listener);
    }

    public void removeListener(final IPropertyEventListener listener) {
        eventListeners.remove(IPropertyEventListener.class, listener);
    }

    protected void onValueSet(final Object caller, final EventKind eventKind) {
        final PropertyEvent event = new PropertyEvent(eventKind, this);
        for (final IPropertyEventListener listener : eventListeners.getListeners(IPropertyEventListener.class)) {
            listener.propertyModified(caller, event);
        }
    }

    public void dispose() {
        // no op
    }

    @Override
    public String toString() {
        return "Property " + name;
    }

}
