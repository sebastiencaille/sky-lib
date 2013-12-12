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
package org.skymarshall.example.hmi.controller.impl;

import org.skymarshall.example.hmi.TestObject;
import org.skymarshall.hmi.mvc.HmiController;
import org.skymarshall.hmi.mvc.PropertyGroup;

public class ControllerExampleController extends HmiController {

    private final ControllerExampleModel model                = new ControllerExampleModel(this);
    private final PropertyGroup          modelPropertiesGroup = new PropertyGroup();

    public ControllerExampleController() {
        modelPropertiesGroup.addProperty(model.getListSelectionProperty());

        model.getTableModel().insert(new TestObject("World", 2));
        model.getTableModel().insert(new TestObject("Hello", 1));
    }

    public ControllerExampleModel getModel() {
        return model;
    }

    @Override
    public void start() {
        propertySupport.attachAll();
    }

    public PropertyGroup getModelValuesGroup() {
        return modelPropertiesGroup;
    }

}
