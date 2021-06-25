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
import static ch.skymarshall.gui.mvc.factories.BindingDependencies.preserveOnUpdateOf;
import static ch.skymarshall.gui.mvc.factories.Converters.guiErrorToString;
import static ch.skymarshall.gui.mvc.factories.Converters.intToString;
import static ch.skymarshall.gui.mvc.factories.Converters.toSingleLine;
import static ch.skymarshall.gui.swing.factories.SwingBindings.selected;
import static ch.skymarshall.gui.swing.factories.SwingBindings.selection;
import static ch.skymarshall.gui.swing.factories.SwingBindings.value;
import static ch.skymarshall.gui.swing.factories.SwingBindings.values;

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

	private static final String NO_ERROR = "No Error";
	private static final long serialVersionUID = -7524991791160097387L;
	private final Container mainContainer;
	private int row = 0;

	public ControllerExampleView(final ControllerExampleController controller) {

		final ControllerExampleModel model = controller.getModel();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().setBackground(Color.WHITE);

		mainContainer = new JPanel(new GridBagLayout());

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 5, 0, 5);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		getContentPane().add(mainContainer, constraints);

		// ------------------------------------------
		// Checkbox input
		// ------------------------------------------
		final BooleanProperty booleanProperty = model.getBooleanPropProperty();

		final JCheckBox booleanEditor = new JCheckBox();
		booleanEditor.setName("booleanEditor");
		booleanProperty.bind(selected(booleanEditor));

		final JLabel booleanEditorCheck = new JLabel("Am I enabled?");
		booleanEditorCheck.setName("booleanEditorCheck");
		booleanProperty.listenActive(booleanEditorCheck::setEnabled);

		final JLabel booleanCounter = new JLabel();
		booleanProperty.bind(counter()).listen(booleanCounter::setText);

		addGuiLineItem(booleanProperty, booleanEditor, booleanEditorCheck, booleanCounter);

		// ------------------------------------------
		// Int input field
		// ------------------------------------------
		final IntProperty intProperty = model.getIntPropProperty();

		final JTextField intStringEditor = new JTextField();
		intStringEditor.setName("intStringEditor");
		intProperty.bind(intToString()).bind(value(intStringEditor));

		final JLabel intCheck = new JLabel();
		intCheck.setName("intCheck");
		intProperty.bind(intToString()).listen(intCheck::setText);

		final JLabel intCounter = new JLabel();
		intProperty.bind(counter()).listen(intCounter::setText);

		addGuiLineItem(intProperty, intStringEditor, intCheck, intCounter);

		// ------------------------------------------
		// String input field
		// ------------------------------------------
		final ObjectProperty<String> stringProperty = model.getStringPropProperty();

		final JTextField stringEditor = new JTextField();
		stringProperty.bind(value(stringEditor));

		final JLabel stringCheck = new JLabel();
		stringProperty.listenActive(stringCheck::setText);

		final JLabel stringCounter = new JLabel();
		stringProperty.bind(counter()).listen(stringCounter::setText);

		addGuiLineItem(stringProperty, stringEditor, stringCheck, stringCounter);

		// ------------------------------------------
		// Item selection
		// ------------------------------------------
		final ObjectProperty<String> staticListSelection = model.getStaticListSelectionProperty();

		final JList<String> staticListEditor = new JList<>(new String[] { "A", "B", "C" });
		staticListEditor.setName("staticListEditor");
		staticListSelection.bind(selection(staticListEditor));

		final JLabel staticListSelectionCheck = new JLabel();
		staticListSelectionCheck.setName("staticListSelectionCheck");
		staticListSelection.listenActive(staticListSelectionCheck::setText);

		final JLabel selectionCounter = new JLabel();
		staticListSelection.bind(counter()).listen(selectionCounter::setText);

		final JScrollPane itemEditorPane = new JScrollPane(staticListEditor);
		itemEditorPane.setPreferredSize(new Dimension(200, 100));

		addGuiLineItem(staticListSelection, itemEditorPane, staticListSelectionCheck, selectionCounter);

		// ------------------------------------------
		// Selection of list which content is based on "Item selection" (preserve
		// selection)
		// ------------------------------------------
		final JList<String> dynamicListEditor = new JList<>();
		dynamicListEditor.setName("dynamicListEditor");
		staticListSelection.bind(new DynamicListContentConverter()).bind(values(dynamicListEditor));

		final ObjectProperty<String> dynamicListSelectionProperty = model.getDynamicListObjectProperty();
		dynamicListSelectionProperty.bind(selection(dynamicListEditor))
				.addDependency(preserveOnUpdateOf(staticListSelection));

		final JLabel dynamicListSelectionCheck = new JLabel();
		dynamicListSelectionCheck.setName("dynamicListSelectionCheck");
		dynamicListSelectionProperty.listenActive(dynamicListSelectionCheck::setText);

		final JLabel dynamicListSelectionCounter = new JLabel();
		dynamicListSelectionProperty.bind(counter()).listen(dynamicListSelectionCounter::setText);

		final JScrollPane dynamicListPane = new JScrollPane(dynamicListEditor);
		dynamicListPane.setPreferredSize(new Dimension(200, 100));
		addGuiLineItem(dynamicListSelectionProperty, dynamicListPane, dynamicListSelectionCheck,
				dynamicListSelectionCounter);

		// ------------------------------------------
		// Table example
		// ------------------------------------------
		final ObjectProperty<TestObject> tableObjectProperty = model.getComplexProperty();

		final TestObjectTableModel tableSelectionTableModel = new TestObjectTableModel(model.getTableModel());
		final JTable tableSelectionEditor = new JTable(tableSelectionTableModel);
		tableSelectionEditor.setName("tableSelectionEditor");
		tableObjectProperty.bind(selection(tableSelectionEditor, tableSelectionTableModel));

		final JLabel tableSelectionCheck = new JLabel();
		tableSelectionCheck.setName("tableSelectionCheck");
		tableObjectProperty.bind(testObjectToString()).listen(tableSelectionCheck::setText);

		final JLabel tableSelectionCounter = new JLabel();
		tableObjectProperty.bind(counter()).listen(tableSelectionCounter::setText);

		final JScrollPane tableEditorPane = new JScrollPane(tableSelectionEditor);
		tableEditorPane.setPreferredSize(new Dimension(200, 70));
		addGuiLineItem(tableObjectProperty, tableEditorPane, tableSelectionCheck, tableSelectionCounter);

		// ------------------------------------------
		// Display of errors
		// ------------------------------------------
		final ErrorProperty errorProperty = model.getErrorProperty();
		final JLabel errorLabel = new JLabel(NO_ERROR);
		errorLabel.setName("errorLabel");
		final JLabel errorCounter = new JLabel();
		errorProperty.bind(guiErrorToString(NO_ERROR)).bind(toSingleLine()).listen(errorLabel::setText);
		errorProperty.bind(counter()).listen(errorCounter::setText);
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
		mainContainer.add(panel, constraints);
		constraints.gridwidth = 1;

		// Widget
		constraints.gridx = 0;
		constraints.gridy = row++;
		constraints.insets = new Insets(0, 5, 5, 5);
		mainContainer.add(editor, constraints);
		constraints.insets = new Insets(0, 0, 0, 0);

		constraints.gridx++;
		counterLabel.setPreferredSize(new Dimension(30, -1));
		mainContainer.add(counterLabel, constraints);

		constraints.gridx++;
		constraints.weightx = 0.0;
		if (check != null) {
			check.setPreferredSize(new Dimension(100, -1));
			mainContainer.add(check, constraints);
		}

		final JPanel gap = new JPanel();
		gap.setPreferredSize(new Dimension(1, 5));
		gap.setBackground(Color.WHITE);
		constraints.gridx = 0;
		constraints.gridy = row++;
		constraints.gridwidth = 5;
		constraints.fill = GridBagConstraints.HORIZONTAL;

		mainContainer.add(gap, constraints);

		constraints.gridx++;

	}
}
