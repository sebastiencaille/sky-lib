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
package ch.scaille.example.gui.controller.impl;

import static ch.scaille.example.gui.TestObject.testObjectToString;
import static ch.scaille.gui.swing.factories.SwingBindings.selected;
import static ch.scaille.gui.swing.factories.SwingBindings.selection;
import static ch.scaille.gui.swing.factories.SwingBindings.value;
import static ch.scaille.gui.swing.factories.SwingBindings.values;
import static ch.scaille.javabeans.BindingDependencies.preserveOnUpdateOf;
import static ch.scaille.javabeans.Converters.guiErrorToString;
import static ch.scaille.javabeans.Converters.intToString;
import static ch.scaille.javabeans.Converters.listen;
import static ch.scaille.javabeans.Converters.mapContains;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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

import ch.scaille.example.gui.TestObjectTableModel;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.ErrorSet;
import ch.scaille.util.helpers.JavaExt;

public class ControllerExampleView extends JFrame {

	private static final long serialVersionUID = -7524991791160097387L;
	private final JPanel mainContainer;
	private final ErrorSet errorProperty;
	private int row = 0;

	public ControllerExampleView(final ControllerExampleController controller) {

		final var model = controller.getModel();
		errorProperty = (ErrorSet) model.getErrorNotifier();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().setBackground(Color.WHITE);

		mainContainer = new JPanel(new GridBagLayout());

		final var constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 5, 0, 5);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		getContentPane().add(mainContainer, constraints);

		// ------------------------------------------
		// Checkbox input
		// ------------------------------------------
		final var booleanProperty = model.getBooleanPropProperty();

		final var booleanEditor = new JCheckBox();
		booleanEditor.setName("booleanEditor");
		booleanProperty.bind(selected(booleanEditor));

		final var booleanEditorCheck = new JLabel("Am I enabled?");
		booleanEditorCheck.setName("booleanEditorCheck");
		booleanProperty.listenActive(booleanEditorCheck::setEnabled);

		final var booleanCounter = new JLabel();
		booleanProperty.bind(counter()).listen(booleanCounter::setText);

		addGuiLineItem(booleanProperty, booleanEditor, booleanEditorCheck, booleanCounter);

		// ------------------------------------------
		// Int input field
		// ------------------------------------------
		final var intProperty = model.getIntPropProperty();

		final var intStringEditor = new JTextField();
		intStringEditor.setName("intStringEditor");
		intProperty.bind(intToString()).bind(value(intStringEditor));

		final var intCheck = new JLabel();
		intCheck.setName("intCheck");
		intProperty.bind(intToString()).listen(intCheck::setText);

		final JLabel intCounter = new JLabel();
		intProperty.bind(counter()).listen(intCounter::setText);

		addGuiLineItem(intProperty, intStringEditor, intCheck, intCounter);

		// ------------------------------------------
		// String input field
		// ------------------------------------------
		final var stringProperty = model.getStringPropProperty();

		final var stringEditor = new JTextField();
		stringProperty.bind(value(stringEditor));

		final var stringCheck = new JLabel();
		stringProperty.listenActive(stringCheck::setText);

		final var stringCounter = new JLabel();
		stringProperty.bind(counter()).listen(stringCounter::setText);

		addGuiLineItem(stringProperty, stringEditor, stringCheck, stringCounter);

		// ------------------------------------------
		// Item selection
		// ------------------------------------------
		final var staticListSelection = model.getStaticListSelectionProperty();

		final var staticListEditor = new JList<>(new String[] { "A", "B", "C" });
		staticListEditor.setName("staticListEditor");
		staticListSelection.bind(selection(staticListEditor));

		final var staticListSelectionCheck = new JLabel();
		staticListSelectionCheck.setName("staticListSelectionCheck");
		staticListSelection.listenActive(staticListSelectionCheck::setText);

		final var selectionCounter = new JLabel();
		staticListSelection.bind(counter()).listen(selectionCounter::setText);

		final var itemEditorPane = new JScrollPane(staticListEditor);
		itemEditorPane.setPreferredSize(new Dimension(200, 100));

		addGuiLineItem(staticListSelection, itemEditorPane, staticListSelectionCheck, selectionCounter);

		// ------------------------------------------
		// Selection of list which content is based on "Item selection" (preserve
		// selection)
		// ------------------------------------------
		final var dynamicListEditor = new JList<String>();
		dynamicListEditor.setName("dynamicListEditor");
		staticListSelection.bind(new DynamicListContentConverter()).bind(values(dynamicListEditor));

		final var dynamicListSelectionProperty = model.getDynamicListObjectProperty();
		dynamicListSelectionProperty.bind(selection(dynamicListEditor))
				.addDependency(preserveOnUpdateOf(staticListSelection));

		final var dynamicListSelectionCheck = new JLabel();
		dynamicListSelectionCheck.setName("dynamicListSelectionCheck");
		dynamicListSelectionProperty.listenActive(dynamicListSelectionCheck::setText);

		final var dynamicListSelectionCounter = new JLabel();
		dynamicListSelectionProperty.bind(counter()).listen(dynamicListSelectionCounter::setText);

		final var dynamicListPane = new JScrollPane(dynamicListEditor);
		dynamicListPane.setPreferredSize(new Dimension(200, 100));
		addGuiLineItem(dynamicListSelectionProperty, dynamicListPane, dynamicListSelectionCheck,
				dynamicListSelectionCounter);

		// ------------------------------------------
		// Table example
		// ------------------------------------------
		final var tableObjectProperty = model.getComplexProperty();

		final var tableSelectionTableModel = new TestObjectTableModel(model.getTableModel());
		final var tableSelectionEditor = new JTable(tableSelectionTableModel);
		tableSelectionEditor.setName("tableSelectionEditor");
		tableObjectProperty.bind(selection(tableSelectionEditor, tableSelectionTableModel));

		final var tableSelectionCheck = new JLabel();
		tableSelectionCheck.setName("tableSelectionCheck");
		tableObjectProperty.bind(testObjectToString()).listen(tableSelectionCheck::setText);

		final var tableSelectionCounter = new JLabel();
		tableObjectProperty.bind(counter()).listen(tableSelectionCounter::setText);

		final var tableEditorPane = new JScrollPane(tableSelectionEditor);
		tableEditorPane.setPreferredSize(new Dimension(200, 70));
		addGuiLineItem(tableObjectProperty, tableEditorPane, tableSelectionCheck, tableSelectionCounter);

		final var fillerConstraints = new GridBagConstraints();
		fillerConstraints.gridx = 1;
		fillerConstraints.gridy = 1;
		fillerConstraints.fill = GridBagConstraints.BOTH;
		fillerConstraints.weightx = 1.0;
		fillerConstraints.weighty = 1.0;
		final var filler = new JPanel();
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
			throw JavaExt.notImplemented();
		}

		@Override
		public String convertPropertyValueToComponentValue(final T propertyValue) {
			return String.valueOf(++count);
		}
	}

	private static <T> CounterBinding<T> counter() {
		return new CounterBinding<>();
	}

	public void addGuiLineItem(final AbstractProperty property, final JComponent editor, final JComponent propertyCheck,
			final JLabel counterLabel) {

		editor.setOpaque(false);

		final var constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = row++;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.0;

		// Title
		final var panel = new JPanel(new FlowLayout(FlowLayout.LEADING)) {
			@Override
			protected void paintComponent(final java.awt.Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				final var y = getHeight() / 2;
				g.drawLine(5, y, getWidth() - 10, y);
			}

			@Override
			protected void paintChildren(final Graphics g) {
				g.translate(5, 0);
				super.paintChildren(g);
				g.translate(-5, 0);
			}
		};
		final var title = new JLabel(property.getName());
		title.setOpaque(true);
		title.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		panel.add(title);

		constraints.gridwidth = 5;
		mainContainer.add(panel, constraints);
		constraints.gridwidth = 1;

		// Value widget
		constraints.gridx = 0;
		constraints.gridy = row++;
		constraints.insets = new Insets(0, 5, 5, 5);
		mainContainer.add(editor, constraints);
		constraints.insets = new Insets(0, 0, 0, 0);

		// Change counter
		constraints.gridx++;
		counterLabel.setPreferredSize(new Dimension(30, -1));
		mainContainer.add(counterLabel, constraints);

		// Property check widget
		constraints.gridx++;
		constraints.weightx = 0.0;
		if (propertyCheck != null) {
			propertyCheck.setPreferredSize(new Dimension(100, -1));
			mainContainer.add(propertyCheck, constraints);
		}

		// Error handling
		errorProperty.getErrors().bind(listen(e -> e.get(property))).bind(guiErrorToString())
				.listen(editor::setToolTipText);
		errorProperty.getErrors().bind(mapContains(property, Color.RED, editor.getForeground()))
				.listen(editor::setForeground);

		final var gap = new JPanel();
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
