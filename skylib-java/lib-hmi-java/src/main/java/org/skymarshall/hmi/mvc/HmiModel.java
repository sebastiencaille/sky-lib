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

import org.skymarshall.hmi.mvc.properties.ErrorProperty;

public class HmiModel {

    protected final ControllerPropertyChangeSupport propertySupport;
    protected final ErrorProperty                   errorProperty;

    public HmiModel(final HmiController controller) {
        this.propertySupport = controller.getPropertySupport();
        this.errorProperty = createErrorProperty("InputError", propertySupport);
    }

    public HmiModel(final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty) {
        this.propertySupport = propertySupport;
        this.errorProperty = errorProperty;
    }

    public HmiModel(final ControllerPropertyChangeSupport propertySupport) {
        this.propertySupport = propertySupport;
        this.errorProperty = createErrorProperty("InputError", propertySupport);
    }

    public ErrorProperty getErrorProperty() {
        return errorProperty;
    }

    protected static ErrorProperty createErrorProperty(final String name, final ControllerPropertyChangeSupport support) {
        return new ErrorProperty(name, support, null);
    }
}
