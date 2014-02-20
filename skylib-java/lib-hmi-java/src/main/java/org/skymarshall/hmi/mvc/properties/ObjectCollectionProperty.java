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

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Set;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;

public class ObjectCollectionProperty<T> extends ObjectProperty<Collection<T>> {

    public ObjectCollectionProperty(final String name, final ControllerPropertyChangeSupport propertySupport) {
        super(name, propertySupport);

    }

    @Override
    public void setValue(final Object caller, final Collection<T> newValue) {
        final Collection<T> oldValue = getValue();
        if (!strictEquals(oldValue, newValue)) {
            super.setValue(caller, newValue);
        }
    }

    private boolean strictEquals(final Collection<T> oldValue, final Collection<T> newValue) {
        if (newValue == null && oldValue == null) {
            return true;
        } else if (oldValue == null || newValue == null) {
            return false;
        } else if (newValue.size() != oldValue.size()) {
            return false;
        }
        final IdentityHashMap<T, T> identityHashMap = new IdentityHashMap<T, T>();
        for (final T old : oldValue) {
            identityHashMap.put(old, old);
        }
        final Set<T> set = identityHashMap.keySet();
        set.removeAll(newValue);
        return set.isEmpty();
    }

}
