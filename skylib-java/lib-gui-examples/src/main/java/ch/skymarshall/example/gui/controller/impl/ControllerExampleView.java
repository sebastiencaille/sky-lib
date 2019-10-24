/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.example.gui.controller.impl;

import static ch.skymarshall.example.gui.TestObject.testObjectToString;
import static ch.skymarshall.gui.mvc.ChainDependencies.detachOnUpdateOf;
import static ch.skymarshall.gui.mvc.converters.Converters.guiErrorToString;
import static ch.skymarshall.gui.mvc.converters.Converters.intToString;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.selected;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.selection;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.value;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.values;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import ch.skymarshall.example.gui.TestObject;
import ch.skymarshall.example.gui.TestObjectTableModel;
import ch.skymarshall.gui.mvc.converters.IConverter;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.gui.mvc.properties.BooleanProperty;
import ch.skymarshall.gui.mvc.properties.ErrorProperty;
import ch.skymarshall.gui.mvc.properties.IntProperty;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;

public class ControllerExampleView extends JFrame {

	private static final long serialVersionUID = -7524991791160097387L;
	private final Container container;
	private int row = 0;

	public ControllerExampleView(final ControllerExampleController controller) {

		final ControllerExampleModel model = controller.getModel();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().setBackground(Color.WHITE);

		container = new JPanel(new GridBagLayout());

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 5, 0, 5);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		getContentPane().add(container, constraints);

		// Checkbox input
		final BooleanProperty booleanProperty = model.getBooleanPropProperty();
		final JCheckBox booleanEditor = new JCheckBox();
		final JLabel label = new JLabel("Am I enabled?");
		final JLabel booleanCounter = new JLabel();
		booleanProperty.bind(selected(booleanEditor));
		booleanProperty.bindWO(label::setEnabled);
		booleanProperty.bind(counter()).bindSetter(booleanCounter::setText);
		addGuiLineItem(booleanProperty, booleanEditor, label, booleanCounter);

		// Int input field
		final IntProperty intProperty = model.getIntPropProperty();
		final JTextField intStringEditor = new JTextField();
		final JLabel intCheck = new JLabel();
		final JLabel intCounter = new JLabel();

		intProperty.bind(intToString()).bind(value(intStringEditor));
		intProperty.bind(intToString()).bindSetter(intCheck::setText);
		intProperty.bind(counter()).bindSetter(intCounter::setText);

		addGuiLineItem(intProperty, intStringEditor, intCheck, intCounter);

		// String input field
		final ObjectProperty<String> stringProperty = model.getStringPropProperty();
		final JTextField stringEditor = new JTextField();
		final JLabel stringCheck = new JLabel();
		final JLabel stringCounter = new JLabel();
		stringProperty.bind(value(stringEditor));
		stringProperty.bindWO(stringCheck::setText);
		stringProperty.bind(counter()).bindSetter(stringCounter::setText);
		addGuiLineItem(stringProperty, stringEditor, stringCheck, stringCounter);

		// Item selection
		final JList<String> listSelectionEditor = new JList<>(new String[] { "A", "B", "C" });
		final JLabel selectionCheck = new JLabel();
		final JLabel selectionCounter = new JLabel();

		final ObjectProperty<String> listSelectedObjectProperty = model.getListSelectedObjectProperty();
		listSelectedObjectProperty.bind(selection(listSelectionEditor));
		listSelectedObjectProperty.bindWO(selectionCheck::setText);
		listSelectedObjectProperty.bind(counter()).bindSetter(selectionCounter::setText);

		final JScrollPane itemEditorPane = new JScrollPane(listSelectionEditor);
		itemEditorPane.setPreferredSize(new Dimension(200, 100));
		addGuiLineItem(listSelectedObjectProperty, itemEditorPane, selectionCheck, selectionCounter);

		// Selection of list which content is based on "Item selection"

		final JList<String> dynamicListSelectionEditor = new JList<>();
		final JLabel dynamicListSelectionCheck = new JLabel();
		final JLabel dynamicListSelectionCounter = new JLabel();

		listSelectedObjectProperty.bind(new DynamicListContentConverter()).bind(values(dynamicListSelectionEditor));

		final ObjectProperty<String> dynamicListSelectionProperty = model.getDynamicListObjectProperty();
		dynamicListSelectionProperty.bind(selection(dynamicListSelectionEditor))
				.addDependency(detachOnUpdateOf(listSelectedObjectProperty));
		dynamicListSelectionProperty.bindWO(dynamicListSelectionCheck::setText);
		dynamicListSelectionProperty.bind(counter()).bindSetter(dynamicListSelectionCounter::setText);

		final JScrollPane dynamicListPane = new JScrollPane(dynamicListSelectionEditor);
		dynamicListPane.setPreferredSize(new Dimension(200, 100));
		addGuiLineItem(dynamicListSelectionProperty, dynamicListPane, dynamicListSelectionCheck,
				dynamicListSelectionCounter);

		// Table example
		final TestObjectTableModel tableSelectionTableModel = new TestObjectTableModel(model.getTableModel());
		final JTable tableSelectionEditor = new JTable(tableSelectionTableModel);
		final JLabel tableSelectionCheck = new JLabel();
		final JLabel tableSelectionCounter = new JLabel();

		final ObjectProperty<TestObject> tableObjectProperty = model.getComplexProperty();
		tableObjectProperty.bind(selection(tableSelectionEditor, tableSelectionTableModel));
		tableObjectProperty.bind(testObjectToString()).bindSetter(tableSelectionCheck::setText);
		tableObjectProperty.bind(counter()).bindSetter(tableSelectionCounter::setText);

		final JScrollPane tableEditorPane = new JScrollPane(tableSelectionEditor);
		tableEditorPane.setPreferredSize(new Dimension(200, 70));
		addGuiLineItem(tableObjectProperty, tableEditorPane, tableSelectionCheck, tableSelectionCounter);

		// Display of errors
		final ErrorProperty errorProperty = model.getErrorProperty();
		final JLabel errorLabel = new JLabel("No Error");
		final JLabel errorCounter = new JLabel();
		errorProperty.bind(guiErrorToString()).bindSetter(errorLabel::setText);
		errorProperty.bind(counter()).bindSetter(errorCounter::setText);
		addGuiLineItem(errorProperty, errorLabel, null, errorCounter);

		final GridBagConstraints fillerConstraints = new GridBagConstraints();
		fillerConstraints.gridx = 1;
		fillerConstraints.gridy = 1;
		fillerConstraints.fill = GridBagConstraints.BOTH;
		fillerConstraints.weightx = 1.0;
		fillerConstraints.weighty = 1.0;
		final JPanel filler = new JPanel();
		filler.setPreferredSize(new Dimension(0, 0));
		filler.setOpaque(false);
		getContentPane().add(filler, fillerConstraints);

		controller.activate();

		validate();
		pack();
	}

	private static class CounterBinding<T> implements IConverter<T, String> {

		private int count = 0;

		@Override
		public T convertComponentValueToPropertyValue(final String componentValue) {
			return null;
		}

		@Override
		public String convertPropertyValueToComponentValue(final T propertyValue) {
			return String.valueOf(++count);
		}
	}

	private static <T> CounterBinding<T> counter() {
		return new CounterBinding<>();
	}

	public void addGuiLineItem(final AbstractProperty property, final JComponent editor, final JComponent check,
			final JLabel counterLabel) {

		editor.setOpaque(false);

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = row++;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.0;

		// Title
		final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING)) {
			@Override
			protected void paintComponent(final java.awt.Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				final Rectangle clipBounds = g.getClipBounds();
				final int y = clipBounds.height / 2;
				g.drawLine(5, y, getWidth() - 10, y);
			}

			@Override
			protected void paintChildren(final Graphics g) {
				g.translate(5, 0);
				super.paintChildren(g);
				g.translate(-5, 0);
			}
		};
		final JLabel title = new JLabel(property.getName());
		title.setOpaque(true);
		title.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		panel.add(title);

		constraints.gridwidth = 5;
		container.add(panel, constraints);
		constraints.gridwidth = 1;

		// Editor
		constraints.gridx = 0;
		constraints.gridy = row++;
		constraints.insets = new Insets(0, 5, 5, 5);
		container.add(editor, constraints);
		constraints.insets = new Insets(0, 0, 0, 0);

		constraints.gridx++;
		counterLabel.setPreferredSize(new Dimension(30, -1));
		container.add(counterLabel, constraints);

		constraints.gridx++;
		constraints.weightx = 0.0;
		if (check != null) {
			check.setPreferredSize(new Dimension(100, -1));
			container.add(check, constraints);
		}

		final JPanel gap = new JPanel();
		gap.setPreferredSize(new Dimension(1, 5));
		gap.setBackground(Color.WHITE);
		constraints.gridx = 0;
		constraints.gridy = row++;
		constraints.gridwidth = 5;
		constraints.fill = GridBagConstraints.HORIZONTAL;

		container.add(gap, constraints);

		constraints.gridx++;

	}
}