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

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.swing16.model.ListModelTableModel;

public class SwingBindings {

    public static IComponentBinding<Boolean> enabled(final JComponent component) {
        return new IComponentBinding<Boolean>() {
            @Override
            public void setComponentValue(final AbstractProperty source, final Boolean value) {
                component.setEnabled(value.booleanValue());
            }

            @Override
            public Object getComponent() {
                return component;
            }

            @Override
            public void addComponentValueChangeListener(final IComponentLink<Boolean> converter) {
                // no op
            }
        };
    }

    public static IComponentBinding<Boolean> value(final JCheckBox editor) {
        return new JCheckBoxBinding(editor);
    }

    public static IComponentBinding<String> value(final JLabel editor) {
        return new JLabelBinding(editor);
    }

    public static JTextFieldBinding value(final JTextField component) {
        return new JTextFieldBinding(component);
    }

    public static IComponentBinding<String> value(final JTextArea component, final boolean readOnly) {
        return new JTextAreaBinding(component, readOnly);
    }

    public static <T> IComponentBinding<T> selection(final JList<T> editor, final Class<T> contentType) {
        return new JListSelectionBinding<T>(editor, contentType);
    }

    public static <T> IComponentBinding<List<T>> values(final JList<T> editor) {
        return new JListContentBinding<T>(editor);
    }

    public static IComponentBinding<Boolean> state(final JCheckBox component) {
        return new JCheckBoxBinding(component);
    }

    public static <T> IComponentBinding<T> selection(final JTable editor, final ListModelTableModel<T, ?> tableModel) {
        return new JTableSelectionBinding<T>(editor, tableModel);
    }

    public static <T> IComponentBinding<List<T>> multipleSelection(final JTable editor,
            final ListModelTableModel<T, ?> tableModel) {
        return new JTableMultiSelectionBinding<T>(editor, tableModel);
    }

    public static <T> IComponentBinding<List<T>> values(final JComboBox<T> component) {
        return new JComboBoxValuesBinding<T>(component);
    }

    public static <T> IComponentBinding<T> selection(final JComboBox<T> component) {
        return new JComboBoxSelectionBinding<T>(component);
    }

}
