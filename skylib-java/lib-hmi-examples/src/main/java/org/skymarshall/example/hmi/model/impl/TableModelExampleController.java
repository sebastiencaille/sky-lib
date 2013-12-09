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
package org.skymarshall.example.hmi.model.impl;

import org.skymarshall.example.hmi.TestObject;
import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.PropertyGroup;
import org.skymarshall.hmi.mvc.properties.BooleanProperty;
import org.skymarshall.hmi.mvc.properties.ErrorProperty;
import org.skymarshall.hmi.mvc.properties.SelectionProperty;

public class TableModelExampleController {

    private final ControllerPropertyChangeSupport propertySupport = new ControllerPropertyChangeSupport(this);

    private final ErrorProperty                   errorProperty   = new ErrorProperty("Error", propertySupport, null);

    public final BooleanProperty                  reverseOrder    = new BooleanProperty("Order", propertySupport,
                                                                          errorProperty, null);

    public final BooleanProperty                  enableFilter    = new BooleanProperty("Filter", propertySupport,
                                                                          errorProperty, null);

    public final SelectionProperty<TestObject>    objectSelection = new SelectionProperty<TestObject>("Selection",
                                                                          propertySupport, errorProperty, null);

    public final PropertyGroup                    listChangers    = new PropertyGroup();

    public TableModelExampleController() {
        listChangers.addProperty(reverseOrder);
        listChangers.addProperty(enableFilter);
    }

    public void setCreated() {
        propertySupport.startController();
    }

}
