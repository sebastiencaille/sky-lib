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

import org.skymarshall.hmi.mvc.properties.AbstractProperty;

/**
 * Event fired when the property is modified.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 */
public class PropertyEvent {

    public enum EventKind {
        BEFORE, AFTER
    }

    private final EventKind        kind;
    private final AbstractProperty property;

    public PropertyEvent(final EventKind kind, final AbstractProperty property) {
        super();
        this.kind = kind;
        this.property = property;
    }

    public EventKind getKind() {
        return kind;
    }

    public AbstractProperty getProperty() {
        return property;
    }

}
