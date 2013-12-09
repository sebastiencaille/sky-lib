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
package org.skymarshall.hmi.swing16.bindings;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

public class JCheckBoxBinding extends AbstractComponentBinding<Boolean> {

    private final JCheckBox component;

    public JCheckBoxBinding(final JCheckBox component) {
        super(component);
        this.component = component;
    }

    @Override
    public void addComponentValueChangeListener(final IComponentLink<Boolean> converter) {
        component.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent event) {
                converter.setValueFromComponent(component,
                        Boolean.valueOf(event.getStateChange() == ItemEvent.SELECTED));
            }
        });
    }

    @Override
    public void setComponentValue(final AbstractProperty source, final Boolean value) {
        if (value != null && value.booleanValue() != component.getModel().isSelected()) {
            component.setSelected(value.booleanValue());
        } else if (value == null) {
            component.setSelected(false);
        }
    }
}
