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

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextArea;

import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

public class JTextAreaBinding extends AbstractComponentBinding<String> {

    private final JTextArea component;
    private final boolean   readOnly;

    public JTextAreaBinding(final JTextArea component) {
        super(component);
        this.component = component;
        readOnly = false;
    }

    public JTextAreaBinding(final JTextArea component, final boolean readOnly) {
        super(component);
        this.component = component;
        this.readOnly = readOnly;
    }

    @SuppressWarnings("serial")
    @Override
    public void addComponentValueChangeListener(final IComponentLink<String> converter) {
        if (readOnly) {
            return;
        }
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {
                converter.setValueFromComponent(component, component.getText());
            }
        });
    }

    @Override
    public void setComponentValue(final AbstractProperty source, final String value) {
        if (value != null) {
            component.setText(value);
        } else {
            component.setText("");
        }
    }

}
