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

import java.util.Map;
import java.util.Set;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.util.CollectionHelper;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapProperty extends ObjectProperty<Map> {

    public MapProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
            final ErrorProperty errorProperty) {
        super(name, propertySupport, errorProperty);
    }

    public <U, V> Map<U, V> getValue(final Class<U> keyClazz, final Class<V> valueClazz) {
        CollectionHelper.checkContent(getValue().keySet(), keyClazz);
        CollectionHelper.checkContent(getValue().values(), valueClazz);
        return getValue();
    }

    public <U> Set<U> getKeys(final Class<U> keyClazz) {
        CollectionHelper.checkContent(getValue().keySet(), keyClazz);
        return getValue().keySet();
    }
}
