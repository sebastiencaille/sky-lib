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

import java.awt.Container;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.skymarshall.example.hmi.Counter;
import org.skymarshall.example.hmi.TestObject;
import org.skymarshall.example.hmi.TestObjectTableModel;
import org.skymarshall.example.hmi.TestObjectToStringConverter;
import org.skymarshall.hmi.mvc.Actions;
import org.skymarshall.hmi.mvc.converters.Converters;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.mvc.properties.BooleanProperty;
import org.skymarshall.hmi.mvc.properties.ErrorProperty;
import org.skymarshall.hmi.mvc.properties.IntProperty;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.mvc.properties.SelectionProperty;
import org.skymarshall.hmi.swing17.bindings.SwingBindings;

public class ControllerExampleView extends JFrame {

    private static final long serialVersionUID = -7524991791160097387L;
    private final Container   container;

    public ControllerExampleView(final ControllerExampleController controller) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        container = getContentPane();

        getContentPane().setLayout(new GridLayout(7, 4));

        // Checkbox input
        final BooleanProperty booleanProperty = controller.model.getABooleanProperty();
        final JCheckBox booleanEditor = new JCheckBox();
        booleanProperty.bind(SwingBindings.value(booleanEditor));
        addGuiLineItem(booleanProperty, booleanEditor, null);

        // Int input field
        final IntProperty intProperty = controller.model.getAnIntPropertyProperty();

        final JTextField intEditor = new JTextField();
        intProperty.bind(Converters.intToString()).bind(SwingBindings.value(intEditor));
        controller.model.getABooleanProperty().bind(SwingBindings.enabled(intEditor));

        final JLabel intCheck = new JLabel();
        intProperty.bind(Converters.intToString()).bind(SwingBindings.value(intCheck));

        addGuiLineItem(intProperty, intEditor, intCheck);

        // String input field
        final ObjectProperty<String> property = controller.model.getAStringPropertyProperty();
        final JTextField stringEditor = new JTextField();
        property.bind(SwingBindings.value(stringEditor));

        final JLabel stringCheck = new JLabel();
        property.bind(SwingBindings.value(stringCheck));
        addGuiLineItem(property, stringEditor, stringCheck);

        // Item selection
        final SelectionProperty<String> listSelectionProperty = controller.model.getListSelectionProperty();
        final JList<String> selectionEditor = new JList<>(new String[] { "A", "B", "C" });
        listSelectionProperty.bind(SwingBindings.selection(selectionEditor, String.class));

        final JLabel selectionCheck = new JLabel();
        listSelectionProperty.bind(SwingBindings.value(selectionCheck));
        addGuiLineItem(listSelectionProperty, new JScrollPane(selectionEditor), selectionCheck);

        // Selection of list which content is based on "Item selection"

        final SelectionProperty<String> dynamicListSelectionProperty = controller.model
                .getDynamicListSelectionProperty();
        final JList<String> dynamicListSelectionEditor = new JList<>();
        listSelectionProperty.bind(new DynamicListContentConverter()).bind(
                SwingBindings.values(dynamicListSelectionEditor));

        dynamicListSelectionProperty.bind(SwingBindings.selection(dynamicListSelectionEditor, String.class));

        // Restore selection after model update
        controller.getModelValuesGroup().addAction(Actions.restoreAfterUpdate(dynamicListSelectionProperty));

        final JLabel dynamicListSelectioncheck = new JLabel();
        dynamicListSelectionProperty.bind(SwingBindings.value(dynamicListSelectioncheck));

        addGuiLineItem(dynamicListSelectionProperty, new JScrollPane(dynamicListSelectionEditor),
                dynamicListSelectioncheck);

        // Table example
        final SelectionProperty<TestObject> tableSelectionProperty = controller.model.getComplexProperty();
        final TestObjectTableModel tableSelectionTableModel = new TestObjectTableModel(controller.model.getTableModel());
        final JTable tableSelectionEditor = new JTable(tableSelectionTableModel);
        tableSelectionProperty.bind(SwingBindings.selection(tableSelectionEditor, tableSelectionTableModel));

        final JLabel tableSelectionCheck = new JLabel();
        tableSelectionProperty.bind(new TestObjectToStringConverter()).bind(SwingBindings.value(tableSelectionCheck));

        addGuiLineItem(tableSelectionProperty, new JScrollPane(tableSelectionEditor), tableSelectionCheck);

        // Display of errors
        final ErrorProperty errorProperty = controller.getErrorProperty();
        final JLabel errorLabel = new JLabel();
        errorProperty.bind(Converters.hmiErrorToString()).bind(SwingBindings.value(errorLabel));
        addGuiLineItem(errorProperty, errorLabel, null);

        controller.setCreated();

        validate();
        pack();
    }

    public void addGuiLineItem(final AbstractProperty property, final JComponent editor, final JComponent check) {
        final JLabel type = new JLabel(property.getName());
        final JLabel counterLabel = new JLabel();
        container.add(type);
        container.add(editor);
        if (check != null) {
            container.add(check);
        } else {
            container.add(new JPanel());
        }
        container.add(counterLabel);
        final Counter counter = new Counter();
        property.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                counterLabel.setText(Integer.toString(counter.count()));
            }
        });
    }

}
